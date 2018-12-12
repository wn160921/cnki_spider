package xin.wangning.mapper;

import java.util.List;

import xin.wangning.vo.LiteratureNode;

public interface LiteratureNodeMapper {
	public List<LiteratureNode> selectAll();
	public void insert(LiteratureNode literatureNode);
}
