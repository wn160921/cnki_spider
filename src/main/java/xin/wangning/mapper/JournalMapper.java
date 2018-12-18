package xin.wangning.mapper;

import xin.wangning.vo.Journal;

import java.util.List;

public interface JournalMapper {
    public List<Journal> selectAll();
    public void insert(Journal journal);
    public Journal selectByName(String value);
    public List<Journal> selectByRank(Long rank);
}
