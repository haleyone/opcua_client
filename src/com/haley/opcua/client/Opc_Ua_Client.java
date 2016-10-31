/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.haley.opcua.client;

import com.haley.opcua.browse.Read;
import com.haley.opcua.browse.TreeNode;
import com.prosysopc.ua.SecureIdentityException;
import com.prosysopc.ua.ServiceException;
import com.prosysopc.ua.SessionActivationException;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.client.ServerListException;
import com.prosysopc.ua.samples.client.SampleConsoleClient;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;

import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.common.ServiceResultException;
import org.opcfoundation.ua.core.Identifiers;
import org.opcfoundation.ua.core.ReferenceDescription;

/**
 *
 * @author HALEY
 */
public class Opc_Ua_Client extends javax.swing.JFrame implements TreeSelectionListener,MouseListener,ItemListener, ActionListener{
        NodeId nodeId = null;
	static SampleConsoleClient sampleConsoleClient = new SampleConsoleClient();
	DefaultMutableTreeNode rootCtrl; 
//	DefaultMutableTreeNode select;
	TreeNode root = null;
	String serverUrl = null;
	ReferenceDescription selectedNode=null;
        
        
        JPopupMenu jpopupMenu1 = new JPopupMenu();
        JPopupMenu jpopupMenu2 = new JPopupMenu();
        JMenuItem subscribeItem,suscribeEditItem,jmenuItem3,jmenuItem4;
        JMenuItem addDeviceItem;
        JMenuItem editItem,deleteItem;
    
        DefaultTableModel dtm = new DefaultTableModel(new String [] {
                   "subName", "Variable", "Mode", "Sampling Rate", "Value", "Timestamp"
                },7);
    /**
     * Creates new form NewJFrame
     */
    public Opc_Ua_Client() {
        initComponents();
        ComboBox1();
        TableModifyMonitoredItem();
    }
    
    public void createTree(TreeNode node,NodeId nodeId) throws ServiceException, StatusException, ServiceResultException{	
//    	String currentNode = sampleConsoleClient.getCurrentNode(nodeId);
    	List<ReferenceDescription> childSize = sampleConsoleClient.client.getAddressSpace().browse(nodeId);
    	
    	for(int i=0; i<childSize.size(); i++){
//    		System.out.println(childSize.get(i).getBrowseName().getName());
    		TreeNode tnode = new TreeNode(childSize.get(i));
//    		String childName = childSize.get(i).getBrowseName().toString();
    		node.addNode(tnode);
    		NodeId tempNodeId = sampleConsoleClient.client.getAddressSpace().getNamespaceTable().toNodeId(childSize.get(i).getNodeId());
    		createTree(tnode,tempNodeId);
    	}
    }
    
    public void ShowTree(DefaultMutableTreeNode nodeCtrl, TreeNode node){
    	for(int i=0; i<node.size(); i++){
    		DefaultMutableTreeNode t = new DefaultMutableTreeNode(node.getChild(i).getBrowseName().getName());
    		nodeCtrl.add(t);
    		ShowTree(t,node.getChildNode(i));
    	}
    }
    
    public void getAddressItems(NodeId nodeId) throws ServiceException, StatusException, ServiceResultException{
    	root = new TreeNode(null); //初始化Root节点
    	createTree(root,nodeId); //将地址空间节点存入自定义的树结构中
    	
    	rootCtrl = new DefaultMutableTreeNode("Root");
    	ShowTree(rootCtrl,root);
    	
    	jTree1 = new JTree(rootCtrl);
    	jTree1.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);//可以连续选中节点 ，按shift
    	this.jScrollPane1.setViewportView(jTree1);
    	
    	suscribeEditItem = new JMenuItem("编辑");
        jpopupMenu1.add(suscribeEditItem);
        suscribeEditItem.addMouseListener(this);
        suscribeEditItem.addActionListener(this);
        
    	jTree1.addTreeSelectionListener(new TreeSelectionListener(){
            @Override
            public void valueChanged(TreeSelectionEvent e) {
//                int name = jTree1.getSelectionCount();
//                System.out.println(name);
//                String select = jTree1.getLastSelectedPathComponent().toString();
                TreePath selectPathList = jTree1.getSelectionPath();
                if(selectPathList==null){
                	return;
                }
                TreeNode rd2;
                rd2 = root;
//                System.out.println("第"+selectPathList.getPathCount()+"层");
                if(selectPathList.getPathCount() != 1){
            	   for(int i=1; i<selectPathList.getPathCount(); i++){
//                       	System.out.println(root.size());
//                   		System.out.println("有"+rd2.size()+"兄弟");
                       	for(int j=0; j<rd2.size(); j++){
                       		String childName = rd2.getChild(j).getBrowseName().getName();
//                       		System.out.println("childName:"+childName + "----"+selectPathList.getPathComponent(i));
                       		if((selectPathList.getPathComponent(i).toString()).equals(childName)){
//                       			System.out.println(rd2.getChild(j).getBrowseName().getName());
                       			selectedNode = rd2.getChild(j);
                       			
                       			showValueToTable();
                       			
                       			rd2 = rd2.getChildNode(j);
                       			break;
                       		}
                       	}
                   }
                }
            }  
        });
    	jTree1.addMouseListener(new MouseAdapter(){
        	public void mousePressed(MouseEvent event){
//        		TreePath selPath = jTree1.getPathForLocation(event.getX(), event.getY());
//        		System.out.println(selPath+"-------------------------------");
                    DefaultMutableTreeNode selectionNode = (DefaultMutableTreeNode)jTree1.getLastSelectedPathComponent();
                    if(jTree1.getSelectionPath() == null){
                            return ;
                    }
//        		 && jTree1.getSelectionPath() != null
                    if(selectionNode.isLeaf()){
                            if(((event.getModifiers() & InputEvent.BUTTON3_MASK) != 0) && (jTree1.getSelectionCount() > 0)){
                                    showEditMenu(event.getX(),event.getY());
                                    System.out.println("youjian");
                            }
//        			
                    }else{
//        			System.out.println("not leaf");
                    }
        	}
        	private void showEditMenu(int x, int y) {
                jpopupMenu1.show(jTree1,x,y); 
            }
        });
    }
    
    int n;
    JTable table= new JTable();
    DefaultTableModel dtm2=null;
    public void showValueToTable(){
    	if(dtm2 == null){
            dtm2 = new DefaultTableModel(new String [] {
                "NodeId","Attribute","Status", "Value", "Timestamp"
            },5);
    	}else{
            int num = dtm2.getRowCount();
            for(int i = 0; i < num; i++){
            dtm2.removeRow(0);
            }
            jTable1.revalidate();
            dtm2.setRowCount(5);
//            TableColumn firsetColumn = jTable1.getColumnModel().getColumn(0);
//            firsetColumn.setMaxWidth(90);
//            firsetColumn.setMinWidth(90);
            TableColumn firsetColumn2 = jTable1.getColumnModel().getColumn(1);
            firsetColumn2.setMaxWidth(110);
            firsetColumn2.setMinWidth(110);
            TableColumn firsetColumn3 = jTable1.getColumnModel().getColumn(2);
            firsetColumn3.setMaxWidth(70);
            firsetColumn3.setMinWidth(70);
    	}
    	NodeId nodeId;
    	List<Read> listRead;
        try {
            nodeId = sampleConsoleClient.client.getAddressSpace().getNamespaceTable().toNodeId(selectedNode.getNodeId());
//			System.out.println(nodeId+"----------");
            String selectedName = selectedNode.getBrowseName().getName();
            listRead = sampleConsoleClient.read(nodeId);
            System.out.println(listRead.size());

            for(int i=0; i<listRead.size(); i++){
                    NodeId nodeId2 = listRead.get(i).getNodeId();
            String attribute = listRead.get(i).getAttribute();
            String status = listRead.get(i).getStatus();
            String value = listRead.get(i).getValue();
            String time = listRead.get(i).getTimestamp();

            dtm2.setValueAt(nodeId2, i, 0);
            dtm2.setValueAt(attribute, i, 1);
            dtm2.setValueAt(status, i, 2);
            dtm2.setValueAt(value, i, 3);
            dtm2.setValueAt(time, i, 4);
            }
            jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
//            jTable1.setFont(new Font("Dialog",0,18));
            jTable1.setModel(dtm2);
            this.jScrollPane2.setViewportView(jTable1);
        } catch (ServiceResultException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        } catch (ServiceException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        } catch (StatusException e) {

                e.printStackTrace();
        }
    }
    public void ComboBox1(){
        String initAddress = sampleConsoleClient.serverUri;
        System.out.println(initAddress);
        DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
        dcbm.addElement(initAddress);
        jComboBox1.setModel(dcbm);
    } 

    
//    public void TableModify(){
//        DefaultTableModel dtm = new DefaultTableModel(new String [] {
//                "NodeId","Attribute","Status", "Value", "Timestamp"
//            },20);
//        dtm.setValueAt("aaaa", 0, 0);
//        dtm.setValueAt("12313", 0, 1);
//        dtm.setValueAt("String", 0, 2);
//        jTable1.setModel(dtm);
//        
//    }
    public void TableModify2(String subName,List<String> variable, String mode,int samprate){
//        System.out.print("table......");
        System.out.println(dtm.getRowCount());
        int i = 0;
        for(int j=0; j<dtm.getRowCount(); j++){
            if(dtm.getValueAt(j, 0) == null){
                i = j;
                break;
            }
        }
//        System.out.println("第几行为空："+i);
//        System.out.println("variable.sieze:"+variable.size());
//        for(int k=0; k<variable.size(); k++){
//            System.out.print(variable.get(k)+" =====");
//            dtm.setValueAt(subName, i, 0);
//            dtm.setValueAt(variable.get(k), i, 1);
//            dtm.setValueAt(mode, i, 2);
//            dtm.setValueAt(priority, i, 3);
//            dtm.setValueAt(samprate, i, 4);
//            i++;   
//        }
        for(int k=0; k<variable.size(); k++){
//            dtm.removeRow(1);
            dtm.insertRow(i,new Object[]{subName,variable.get(k),mode,samprate,"11","22"});
            i++;   
        }
        jTable2.setModel(dtm);    
    }
//    JPopupMenu[] pms = new JPopupMenu[7];
    JPopupMenu pm = new JPopupMenu();
    public void TableModifyMonitoredItem(){
        jTable2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable2.addMouseListener(new MouseAdapter(){
            
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("1111111");
                if(e.getButton() == MouseEvent.BUTTON3){
                    System.out.println("22222");
                    if(jTable2.rowAtPoint(e.getPoint()) == jTable2.getSelectedRow()){
                        System.out.println(jTable2.getSelectedRow());
//                        pms[jTable2.getSelectedRow()].show(jTable2,e.getX(),e.getY());
                        pm.show(jTable2,e.getX(),e.getY());
                    }
                }
            }
        });
        editItem = new JMenuItem("编辑");
        deleteItem = new JMenuItem("删除");
        pm.add(editItem);
        pm.add(deleteItem);    
        
        deleteItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
//                jTable2.getSelectedRow();
                dtm.removeRow(jTable2.getSelectedRow());
            }
        });
     
    }
    
        /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox<>();
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("OPC UA Client");
        setLocation(new java.awt.Point(500, 150));

        jComboBox1.setEditable(true);
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jCheckBox1.setText("安全模式");

        jButton1.setText("连  接");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jTable1.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "NodeId", "Attribute", "Status", "Value", "Timestamp"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable1.setRowHeight(18);
        jScrollPane2.setViewportView(jTable1);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "subName", "Variable", "Mode", "Sampling Rate", "Value", "Timestamp"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, true, true, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.setRowHeight(18);
        jScrollPane4.setViewportView(jTable2);

        jLabel1.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
        jLabel1.setText("监控项：");

        jMenu1.setText("File");

        jMenuItem1.setText("订阅");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jMenu3.setText("help");
        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel1))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 552, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(71, 71, 71)
                                .addComponent(jCheckBox1)
                                .addGap(55, 55, 55)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 397, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox1)
                    .addComponent(jButton1))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 318, Short.MAX_VALUE)))
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
       
        System.out.println("file订阅");
        Subscribe_1 sub = new Subscribe_1(Opc_Ua_Client.this,rootCtrl,root);
//          sub.SetTree(type);
        sub.setResizable(false);
        sub.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        System.out.println("connect");
        if (nodeId == null)
            nodeId = Identifiers.RootFolder;
        try {
            sampleConsoleClient.initialize();
            sampleConsoleClient.connect();//非安全模式连接服务器
            getAddressItems(nodeId);//获取服务器的地址空间节点，生成目录结构
        } catch (SessionActivationException | URISyntaxException | SecureIdentityException | IOException
                        | ServerListException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }   catch (ServiceException ex) {
                Logger.getLogger(Opc_Ua_Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (StatusException ex) {
                Logger.getLogger(Opc_Ua_Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ServiceResultException ex) {
                Logger.getLogger(Opc_Ua_Client.class.getName()).log(Level.SEVERE, null, ex);
            }

		
        
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
    	
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Opc_Ua_Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Opc_Ua_Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Opc_Ua_Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Opc_Ua_Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Opc_Ua_Client client1=null;
                client1 = new Opc_Ua_Client(); // TODO Auto-generated catch block
		client1.setResizable(false);
                client1.setVisible(true);
                
            }
        });
        
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
    private javax.swing.JTree jTree1;
    @Override
    public void valueChanged(TreeSelectionEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.print("mouse............");
    }

    @Override
    public void mousePressed(MouseEvent e) {
//        int mods = e.getModifiers();
//        if((mods & InputEvent.BUTTON3_MASK)!=0){
//            jpopupMenu1.show(this,e.getX(),e.getY());
//        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      //To change body of generated methods, choose Tools | Templates.
//      System.out.print("addmydevice");
      if(e.getSource() == subscribeItem){
          System.out.println("订阅");
          Subscribe_1 sub = new Subscribe_1(Opc_Ua_Client.this);
//          sub.SetTree(type);
          sub.setVisible(true);
      }else if(e.getSource() == suscribeEditItem){
          System.out.println("编辑");
      }else if(e.getSource() == addDeviceItem){
          System.out.println("add Mydevice");
      }
    }
}
