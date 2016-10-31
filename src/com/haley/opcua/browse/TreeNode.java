package com.haley.opcua.browse;

import java.util.ArrayList;
import java.util.List;

import org.opcfoundation.ua.core.ReferenceDescription;

public class TreeNode {
	private ReferenceDescription entity = null;

	List<TreeNode> nodeList = new ArrayList<TreeNode>();
	
	public TreeNode(ReferenceDescription entity){
		this.entity = entity;
	}
	public TreeNode(){}
	public ReferenceDescription getChild(int index){
		return nodeList.get(index).entity;
	}
	
	public TreeNode getChildNode(int index){
		return nodeList.get(index);
	}
	public int size(){
		return nodeList.size();
	}
	public void addNode(TreeNode n){
//		TreeNode child = new TreeNode();
////		child.setEntity(rd);
		nodeList.add(n);
	}
	public ReferenceDescription getEntity() {
		return entity;
	}

	public void setEntity(ReferenceDescription entity) {
		this.entity = entity;
	}

	public List<TreeNode> getNodeList() {
		return nodeList;
	}

	public void setNodeList(List<TreeNode> nodeList) {
		this.nodeList = nodeList;
	}

	
}
