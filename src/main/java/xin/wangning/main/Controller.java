package xin.wangning.main;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import sun.plugin.javascript.navig.AnchorArray;
import xin.wangning.util.DBHelper;
import xin.wangning.vo.Journal;
import xin.wangning.vo.Literature;
import xin.wangning.vo.Refer;

import java.util.ArrayList;
import java.util.List;

public class Controller {
    private ChromeDriver driver;
    private List<Journal> journalList;

    public Controller() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        journalList = DBHelper.getJournalList();
    }

    private List<String> getJournalUrlList() {
        List<Journal> journalList = DBHelper.getJournalList();
        List<String> urlList = new ArrayList<String>();
        for (Journal journal : journalList) {
            urlList.add(journal.getUrl());
        }
        return urlList;
    }

    public void start() {
        for (Journal url : journalList) {
            crawJournal(url);
        }
    }

    public void crawRefer() {
        List<Literature> literatureList = DBHelper.getAllLiture();
        for (Literature literature : literatureList) {
//            try {
//                crawRefer(literature);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
            crawRefer(literature);
        }
    }

    public void crawOrder() {
        List<Literature> literatureList = DBHelper.getAllNeedOrderLiterature();
        for(Literature literature:literatureList){
            crawOrder(literature);
        }
    }

    private void crawOrder(Literature literature) {
        driver.get(literature.getUrl());
        WebDriverWait webDriverWait = new WebDriverWait(driver, 10);
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("DownLoadParts")));
        WebElement scabDiv = driver.findElement(By.id("DownLoadParts"));
        WebElement aElem = scabDiv.findElement(By.tagName("a"));
        if (aElem.getText().contains("HTML")) {
            trueClick(aElem);
            switchWindow(1);
            List<String> realRefer = getAllTrueReferList();
            List<Refer> referList = DBHelper.getAllReferByName(literature.getName());
            int index = 0;
            while (index<realRefer.size()){
                for(Refer refer:referList){
                    if(realRefer.get(index).contains(refer.getReferName())){
                        refer.setReferOrder(index+1);
                        DBHelper.insertRefer(refer);
                    }
                }
                index++;
            }
            driver.close();
            switchWindow(0);
        }


    }

    private List<String> getAllTrueReferList(){
        List<String> stringList = new ArrayList<String>();
        try {
            WebDriverWait webDriverWait = new WebDriverWait(driver, 10);
            webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("a_bibliography")));
            WebElement refersDiv = driver.findElement(By.id("a_bibliography"));
            List<WebElement> pList = refersDiv.findElements(By.tagName("p"));
            for(WebElement e:pList){
                stringList.add(e.getText());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return stringList;
    }


    private void crawJournal(Journal journal) {

        driver.get(journal.getUrl());
        switchWindow(0);
        WebDriverWait webDriverWait = new WebDriverWait(driver, 20);
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".s-dataList.clearfix")));
        List<WebElement> yearElemList = driver.findElements(By.cssSelector(".s-dataList.clearfix"));
        System.out.println("年数:" + yearElemList.size());
        for (WebElement yearElem : yearElemList) {
            String year = yearElem.findElement(By.tagName("em")).getText();
            try {
                int yearNum = Integer.parseInt(year);
                if (yearNum > 2107 || yearNum < 2013) {
                    continue;
                }
            } catch (Exception e) {
                continue;
            }
            trueClick(yearElem);
            webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".s-dataList.clearfix a")));
            List<WebElement> monthElemList = yearElem.findElements(By.tagName("a"));
            System.out.println("刊数:" + monthElemList.size());
            for (WebElement monthElem : monthElemList) {
                String month = monthElem.getText().replaceAll("No.0|No.", "");
                trueClick(monthElem);
                webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".name a")));
                List<WebElement> literatureElemList = driver.findElements(By.cssSelector(".name a"));
                System.out.println("文献数：" + literatureElemList.size());
                for (WebElement literatureElem : literatureElemList) {
                    trueClick(literatureElem);
                    switchWindow(1);
                    try {
                        Literature literature = crawLierature();
                        DBHelper.dumpLiterature(literature);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    driver.close();
                    switchWindow(0);
                }

            }
        }
    }

    /**
     * 发现未记录期刊就记录
     *
     * @return
     */
    private Literature crawLierature() {
        Literature literature = new Literature();
        literature.setUrl(driver.getCurrentUrl());
        WebDriverWait webDriverWait = new WebDriverWait(driver, 10);
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".title")));
        WebElement titleElem = driver.findElement(By.className("title"));
        literature.setName(titleElem.getAttribute("innerHTML"));

        WebElement sourInfoElem = driver.findElement(By.className("sourinfo"));
        WebElement jnameElem = sourInfoElem.findElement(By.className("title"));
        String jname = jnameElem.getText();
        WebElement jdate = driver.findElement(By.xpath("//*[@id=\"mainArea\"]/div[3]/div[3]/div[2]/div[2]/p[3]"));
        String jdataText = jdate.getText();
        String year = jdataText.substring(0, 4);
        String phase = jdataText.substring(5, 7);
        Journal journal = new Journal();
        journal.setName(jname);
        journal.setUrl("unknow");
        DBHelper.insertJournal(journal);
        literature.setBelong(jname);
        literature.setYear(Integer.parseInt(year));
        literature.setPhase(Integer.parseInt(phase));
        List<WebElement> pList = driver.findElements(By.tagName("p"));
        for (WebElement e : pList) {
            if (e.getText().contains("分类号")) {
                literature.setClassify(e.getText().replace("分类号：", ""));
            }
        }
        try {
            driver.switchTo().frame("frame1");
            webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("pc_CJFQ")));
            WebElement referElem = driver.findElement(By.id("pc_CJFQ"));
            literature.setReferNum(Integer.parseInt(referElem.getAttribute("innerHTML")));
            driver.switchTo().defaultContent();
        } catch (Exception e) {
            literature.setReferNum(-1);
        }
        return literature;
    }

    private void crawRefer(Literature literature) {
        if (literature.getReferNum() <= 0) {
            return;
        }
        driver.get(literature.getUrl());
        WebDriverWait webDriverWait = new WebDriverWait(driver, 20);
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#frame1")));
        List<String> referUrl = new ArrayList<String>();
        driver.switchTo().frame("frame1");
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".essayBox")));
        WebElement referElem = driver.findElement(By.cssSelector(".essayBox"));
        if (literature.getReferNum() > 10) {
            List<WebElement> aList = referElem.findElement(By.id("CJFQ")).findElements(By.tagName("a"));
            List<String> urlList = new ArrayList<String>();
            for (WebElement e : aList) {
                String href = e.getAttribute("href");
                urlList.add(href);
            }
            int index = 0;
            driver.executeScript("window.open('https://www.baidu.com')");
            switchWindow(1);
            while (index <= literature.getReferNum() / 10 + 1) {
                driver.get(urlList.get(index));
                webDriverWait = new WebDriverWait(driver, 10);
                webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".essayBox")));
                WebElement essayElem = driver.findElement(By.cssSelector(".essayBox"));
                WebElement ulElem = essayElem.findElement(By.tagName("ul"));
                List<WebElement> liElem = ulElem.findElements(By.tagName("li"));
                for (WebElement liE : liElem) {
                    try {
                        WebElement a = liE.findElement(By.tagName("a"));
                        referUrl.add(a.getAttribute("href"));
                    } catch (Exception e) {

                    }
                }
                index++;
            }
            driver.close();
            switchWindow(0);
        } else {
            List<WebElement> referElemList = referElem.findElements(By.tagName("li"));
            for (WebElement liE : referElemList) {
                WebElement aElem = liE.findElement(By.tagName("a"));
                referUrl.add(aElem.getAttribute("href"));
            }
        }

        //新建一个窗口
        driver.switchTo().defaultContent();
        driver.executeScript("window.open('https://www.baidu.com')");
        switchWindow(1);
        for (String url : referUrl) {
            driver.get(url);
            Literature refliterature = crawLierature();
            DBHelper.insertLiterature(refliterature);
            Refer refer = new Refer();
            refer.setName(literature.getName());
            refer.setReferName(refliterature.getName());
            DBHelper.insertRefer(refer);
        }
        driver.close();
        switchWindow(0);
        //添加order信息
    }

    private void switchWindow(int num) {
        int index = 0;
        try {
            for (String h : driver.getWindowHandles()) {
                if (index == num) {
                    driver.switchTo().window(h);
                }
                index++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void trueClick(WebElement element) {
        while (true) {
            try {
                Actions actions = new Actions(driver);
                actions.moveToElement(element);
                actions.click(element);
                actions.perform();
                Thread.sleep(1000);
                break;
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }
}
