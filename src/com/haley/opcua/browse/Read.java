package com.haley.opcua.browse;

import java.util.ArrayList;
import java.util.List;

import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.builtintypes.StatusCode;

public class Read {
	private NodeId nodeId;
	private String attribute;
	private String status;
	private String value;
	private String timestamp;
	
	List<Read> readList = new ArrayList<Read>();

	public Read(NodeId nodeId, String attribute, String status, String value, String timestamp) {
		this.nodeId = nodeId;
		this.attribute = attribute;
		this.status = status;
		this.value = value;
		this.timestamp = timestamp;
	}
	public int size(){
		return readList.size();
	}
	public void addReadData(Read read){
		readList.add(read);
	}
	
	public NodeId getNodeId() {
		return nodeId;
	}

	public void setNodeId(NodeId nodeId) {
		this.nodeId = nodeId;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public List<Read> getReadList() {
		return readList;
	}
	public void setReadList(List<Read> readList) {
		this.readList = readList;
	}
	
	
}
