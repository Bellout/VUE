/*
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2003, 2004 
 * Tufts University. All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 */

/*
 * MapFilterModelEditor.java
 *
 * Created on February 15, 2004, 7:10 PM
 */

package tufts.vue.filter;

/**
 *
 * @author  akumar03
 */


import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.awt.*;

import java.util.*;
import java.util.ArrayList;

public class MapFilterModelEditor extends JPanel {
    public static final String MAP_FILTER_INFO = tufts.vue.VueResources.getString("info.filter.map");
    
    MapFilterModel mapFilterModel;
    JTable mapFilterTable;
    AddButtonListener addButtonListener = null;
    DeleteButtonListener deleteButtonListener = null;
    MapFilterModelSelectionListener sListener = null;
    boolean editable = false;
    JButton addButton=new tufts.vue.gui.VueButton("add");
    JButton deleteButton=new tufts.vue.gui.VueButton("delete");
    JLabel questionLabel = new JLabel(tufts.vue.VueResources.getImageIcon("smallInfo"), JLabel.LEFT);
    
    /** Creates a new instance of MapFilterModelEditor */
    public MapFilterModelEditor(MapFilterModel mapFilterModel) {
        this.mapFilterModel = mapFilterModel;
        questionLabel.setToolTipText(this.MAP_FILTER_INFO);
        setMapFilterModelPanel();
        
    }
    private void setMapFilterModelPanel() {
        addButton.setToolTipText("Add Map Filter");
        deleteButton.setToolTipText("Delete Map Filter");
        mapFilterTable = new JTable(mapFilterModel);
        mapFilterTable.addFocusListener(new FocusListener() {
             public void focusLost(FocusEvent e) {
                 if(mapFilterTable.isEditing()) {
                     mapFilterTable.getCellEditor(mapFilterTable.getEditingRow(),mapFilterTable.getEditingColumn()).stopCellEditing();
                 }
                 mapFilterTable.removeEditor();
             }
             public void focusGained(FocusEvent e) {
             }
         });
        mapFilterTable.setPreferredScrollableViewportSize(new Dimension(200,100));
        JScrollPane mapFilterScrollPane=new JScrollPane(mapFilterTable);
        mapFilterScrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        JPanel  mapFilterPanel=new JPanel();
        mapFilterPanel.setLayout(new BorderLayout());
        mapFilterPanel.add( mapFilterScrollPane, BorderLayout.CENTER);
        //mapFilterPanel.setBorder(BorderFactory.createEmptyBorder(3,6,3,6));
        // addConditionButton
        addButtonListener = new AddButtonListener(mapFilterModel);
        addButton.addActionListener(addButtonListener);
        
        sListener= new MapFilterModelSelectionListener(deleteButton, -1);
        mapFilterTable.getSelectionModel().addListSelectionListener(sListener);
        deleteButtonListener = new DeleteButtonListener(mapFilterTable, sListener);
        deleteButton.addActionListener(deleteButtonListener);
        
        deleteButton.setEnabled(false);
        JPanel innerPanel=new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        //innerPanel.setBorder(BorderFactory.createEmptyBorder(2,6,6,6));
        JPanel bottomPanel=new JPanel(new FlowLayout(FlowLayout.RIGHT,2,0));
        //bottomPanel.setBorder(BorderFactory.createEmptyBorder(3,6,3,6));
        bottomPanel.add(addButton);
        bottomPanel.add(deleteButton);
        bottomPanel.add(questionLabel);
        //innerPanel.add(labelPanel);
        innerPanel.add(bottomPanel);
       
        innerPanel.add(mapFilterPanel);
        setLayout(new BorderLayout());
        add(innerPanel,BorderLayout.CENTER);
        //setSize(300, 300);
        validate();
        
    }
    
    public void setMapFilterModel(MapFilterModel mapFilterModel) {
        this.mapFilterModel = mapFilterModel;
        mapFilterTable.setModel(mapFilterModel);
        addButton.removeActionListener(addButtonListener);
        addButtonListener = new AddButtonListener(mapFilterModel);
        addButton.addActionListener(addButtonListener);
        deleteButton.removeActionListener(deleteButtonListener);
        deleteButtonListener = new DeleteButtonListener(mapFilterTable, sListener);
        deleteButton.addActionListener(deleteButtonListener);
    }
    
    
    public class AddButtonListener implements ActionListener {
        private  MapFilterModel model;
        public AddButtonListener(MapFilterModel model) {
            this.model=model;
        }
        public void actionPerformed(ActionEvent e) {
            AddDialog addDialog = new AddDialog(model);
        }
    }
    
    public class AddDialog extends JDialog {
        MapFilterModel model;
        JLabel keyLabel;
        JLabel typeLabel;
        JTextField keyEditor;
        JComboBox typeEditor;
        Vector allTypes;
        
        public AddDialog(MapFilterModel model) {
            super(tufts.vue.VUE.getInstance(),"Add Key",true);
            this.model = model;
            allTypes = (Vector)TypeFactory.getAllTypes();
            keyLabel = new JLabel("Field");
            typeLabel = new JLabel("Type");
            keyEditor = new JTextField();
            typeEditor = new JComboBox(allTypes);
            keyEditor.setPreferredSize(new Dimension(80,20));
            JPanel keyPanel=new JPanel();
            keyPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            keyPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
            keyPanel.add(keyLabel);
            keyPanel.add(keyEditor);
            
            JPanel typePanel=new JPanel();
            typePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            typePanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
            typePanel.add(typeLabel);
            typePanel.add(typeEditor);
            
            // SOUTH: southPanel(cancelButton, okButton)
            
            JButton okButton=new JButton("Ok");
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    updateModelAndNotify();
                    setVisible(false);
                }
            });
            
            JButton cancelButton=new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                }
            });
            
            JPanel southPanel=new JPanel();
            southPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            southPanel.add(okButton);
            southPanel.add(cancelButton);
            BoxLayout layout = new BoxLayout(getContentPane(), BoxLayout.Y_AXIS);
            
            
            
            getContentPane().setLayout(layout);
            getContentPane().add(keyPanel);
            getContentPane().add(typePanel);
            getContentPane().add(southPanel);
            pack();
            setLocation(MapFilterModelEditor.this.getLocationOnScreen());
            show();
            
        }
        
        private void updateModelAndNotify(){
            Key key = new Key(keyEditor.getText(),(Type)typeEditor.getSelectedItem());
            model.addKey(key);
            System.out.println("ADDED KEY of Type = "+((Type)typeEditor.getSelectedItem()).getDisplayName());
            model.fireTableDataChanged();
        }
    }
    
    public class MapFilterModelSelectionListener  implements ListSelectionListener {
        private int m_selectedRow;
        private JButton m_deleteButton;
        
        public MapFilterModelSelectionListener(JButton deleteButton, int selectedRow) {
            m_selectedRow=selectedRow;
            m_deleteButton=deleteButton;
            updateButtons();
        }
        
        public void valueChanged(ListSelectionEvent e) {
            //Ignore extra messages.
            if (e.getValueIsAdjusting()) return;
            
            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            if (lsm.isSelectionEmpty()) {
                m_selectedRow=-1;
            } else {
                m_selectedRow=lsm.getMinSelectionIndex();
            }
            updateButtons();
        }
        
        public int getSelectedRow() {
            return m_selectedRow;
        }
        
        public void setSelectedRow(int row) {
            this.m_selectedRow = row;
        }
        private void updateButtons() {
            if (getSelectedRow()==-1) {
                m_deleteButton.setEnabled(false);
            } else {
                m_deleteButton.setEnabled(true);
            }
        }
    }
    
    public class DeleteButtonListener implements ActionListener {
        private JTable table;
        private MapFilterModelSelectionListener m_sListener;
        
        public DeleteButtonListener(JTable table,MapFilterModelSelectionListener sListener) {
            this.table = table;
            m_sListener=sListener;
        }
        
        public void actionPerformed(ActionEvent e) {
            // will only be invoked if an existing row is selected
            if(JOptionPane.showConfirmDialog(tufts.vue.VUE.getInstance(),"All Statments in nodes with this key will be deleted. Are you sure?","Delete Key",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                int r=m_sListener.getSelectedRow();
                ((MapFilterModel) table.getModel()).remove(r);
                ((MapFilterModel) table.getModel()).fireTableRowsDeleted(r,r);
                if(r> 0)
                    table.setRowSelectionInterval(r-1, r-1);
                else if(table.getRowCount() > 0)
                    table.setRowSelectionInterval(0,0);
            }
        }
    }
}
