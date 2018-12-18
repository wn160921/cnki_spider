package xin.wangning.mapper;

import xin.wangning.vo.Literature;

import java.util.List;

public interface LiteratureMapper {
    public List<Literature> selectAll();
    public void insert(Literature literature);
    public Literature selectByName(String value);
    public void update(Literature literature);
    
    public List<Literature> selectByRank(Long rank);
}
