package xin.wangning.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import xin.wangning.mapper.JournalMapper;
import xin.wangning.mapper.LiteratureMapper;
import xin.wangning.mapper.LiteratureNodeMapper;
import xin.wangning.mapper.ReferMapper;
import xin.wangning.vo.Journal;
import xin.wangning.vo.Literature;
import xin.wangning.vo.LiteratureNode;
import xin.wangning.vo.Refer;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Ref;
import java.util.ArrayList;
import java.util.List;

public class DBHelper {
    private static SqlSessionFactory sqlSessionFactory = null;

    public static SqlSessionFactory getSqlSessionFactory() {
        if (sqlSessionFactory != null) {
            return sqlSessionFactory;
        } else {
            String resource = "mybatis-config.xml";
            InputStream inputStream = null;
            try {
                inputStream = Resources.getResourceAsStream(resource);
            } catch (IOException e) {
                e.printStackTrace();
            }
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            return sqlSessionFactory;
        }
    }

    public static List<Journal> getJournalList() {
        SqlSession sqlSession = getSqlSessionFactory().openSession();
        JournalMapper journalMapper = sqlSession.getMapper(JournalMapper.class);
        List<Journal> journalList = journalMapper.selectAll();
        sqlSession.close();
        return journalList;
    }

    public static void dumpLiterature(Literature literature) {
        SqlSession sqlSession = getSqlSessionFactory().openSession();
        LiteratureMapper literatureMapper = sqlSession.getMapper(LiteratureMapper.class);
        Literature test = literatureMapper.selectByName(literature.getName());
        if (test == null) {
            literatureMapper.insert(literature);
        } else {
            literatureMapper.update(literature);
        }
        sqlSession.commit();
        sqlSession.close();
    }

    public static void insertJournal(Journal journal) {
        SqlSession sqlSession = getSqlSessionFactory().openSession();
        JournalMapper journalMapper = sqlSession.getMapper(JournalMapper.class);
        Journal test = journalMapper.selectByName(journal.getName());
        if (test == null) {
            journalMapper.insert(journal);
        }
        sqlSession.commit();
        sqlSession.close();
    }

    public static void insertLiterature(Literature literature) {
        SqlSession sqlSession = getSqlSessionFactory().openSession();
        LiteratureMapper literatureMapper = sqlSession.getMapper(LiteratureMapper.class);
        Literature test = literatureMapper.selectByName(literature.getName());
        if (test == null) {
            literatureMapper.insert(literature);
        }
        sqlSession.commit();
        sqlSession.close();
    }

    public static void insertRefer(Refer refer) {
        SqlSession sqlSession = getSqlSessionFactory().openSession();
        ReferMapper referMapper = sqlSession.getMapper(ReferMapper.class);
        Refer test = referMapper.selectByTwoName(refer);
        if (test == null) {
            referMapper.insert(refer);
        } else {
            if (!(refer.getReferOrder() == null)) {
                referMapper.update(refer);
            }
        }
        sqlSession.commit();
        sqlSession.close();
    }

    public static List<Literature> getAllLiture() {
        SqlSession sqlSession = getSqlSessionFactory().openSession();
        LiteratureMapper literatureMapper = sqlSession.getMapper(LiteratureMapper.class);
        List<Literature> literatureList = literatureMapper.selectAll();
        sqlSession.close();
        return literatureList;
    }

    public static List<Literature> getAllNeedOrderLiterature(){
        SqlSession sqlSession = getSqlSessionFactory().openSession();
        LiteratureMapper literatureMapper = sqlSession.getMapper(LiteratureMapper.class);
        ReferMapper referMapper = sqlSession.getMapper(ReferMapper.class);
        List<Literature> literatureList = literatureMapper.selectAll();
        List<Refer> referList = referMapper.selectAll();
        List<String> nameList = new ArrayList<String>();
        for(Refer refer:referList){
            if(!nameList.contains(refer.getName())){
                nameList.add(refer.getName());
            }
        }
         List<Literature> need = new ArrayList<Literature>();
        for (Literature literature:literatureList){
            if(nameList.contains(literature.getName())){
                need.add(literature);
            }
        }
        sqlSession.close();
        return need;
    }

    public static List<Refer> getAllReferByName(String name){
        SqlSession sqlSession = getSqlSessionFactory().openSession();
        ReferMapper referMapper = sqlSession.getMapper(ReferMapper.class);
        List<Refer> referList = referMapper.selectByName(name);
        sqlSession.close();
        return referList;
    }
    
    public static void insertLiteratureNode(LiteratureNode literatureNode) {
		SqlSession sqlSession = getSqlSessionFactory().openSession();
		LiteratureNodeMapper literatureNodeMapper = sqlSession.getMapper(LiteratureNodeMapper.class);
		literatureNodeMapper.insert(literatureNode);
		sqlSession.commit();
        sqlSession.close();
	}
    
}
