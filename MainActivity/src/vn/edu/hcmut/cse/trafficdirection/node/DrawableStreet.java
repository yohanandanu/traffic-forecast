package vn.edu.hcmut.cse.trafficdirection.node;

import java.util.ArrayList;

import vn.edu.hcmut.cse.trafficdirection.node.NodeDrawable;

public class DrawableStreet {
	private String m_label = null;
	private String m_type = null;
	private int m_id;
	public ArrayList<NodeDrawable> m_nodeArray = new ArrayList<NodeDrawable>();

	public DrawableStreet() {
		m_nodeArray.clear();
	}

	public String getType() {
		return m_type;
	}

	public void setType(String m_type) {
		this.m_type = m_type;
	}

	public int getId() {
		return m_id;
	}

	public void setId(int m_id) {
		this.m_id = m_id;
	}

	public void addNote(NodeDrawable note) {
		// TODO Auto-generated method stub
		m_nodeArray.add(note);
	}

	public String getLabel() {
		return m_label;
	}

	public void setLabel(String m_label) {
		this.m_label = m_label;
	}
}
