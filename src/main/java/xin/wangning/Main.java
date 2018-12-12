package xin.wangning;

import xin.wangning.main.Controller;

public class Main {
    /**
     * 分成三部分
     * 第一部分爬文献
     * 第二部分爬相关文献
     * 第三部分爬取真实序号
     * @param args
     */
    public static void main(String[] args){
        Controller controller = new Controller();
//        controller.start();
        controller.crawRefer();
    }
}
