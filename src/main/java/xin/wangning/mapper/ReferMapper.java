package xin.wangning.mapper;

import xin.wangning.vo.Refer;

import java.util.List;

public interface ReferMapper {
    public List<Refer> selectAll();
    public void insert(Refer refer);
    public void update(Refer refer);
    public Refer selectByTwoName(Refer refer);
    public List<Refer> selectByName(String value);
}
