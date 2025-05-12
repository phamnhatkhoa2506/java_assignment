package views.admin.common;

import java.awt.event.ActionListener;
import java.util.UUID;
import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import utils.AdminFrameConfig;
import views.admin.panels.BaseManagementPanel;

public class RecordActionButton extends DefaultCellEditor {
    public JButton button;
    private JTable tableRef;

    private AdminFrameConfig.ActionType actionType;
    private BaseManagementPanel parentPanel;
    private boolean isPushed;
    
    public RecordActionButton(JCheckBox checkBox, AdminFrameConfig.ActionType actionType, BaseManagementPanel parentPanel) {
        super(checkBox);
        this.actionType = actionType;
        this.parentPanel = parentPanel;
    
        button = new JButton();
        button.setOpaque(true);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        button.setText((value == null) ? "" : value.toString());
        this.tableRef = table;
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {            
            int selectedRowInView = tableRef.getSelectedRow();
            int selectedRowInModel = tableRef.convertRowIndexToModel(selectedRowInView);
            UUID uuid = null;
            Long longId = null;

            try {
                uuid = (UUID)tableRef.getModel().getValueAt(selectedRowInModel, 0);
            }
            catch (ClassCastException e) {
                longId = (Long)tableRef.getModel().getValueAt(selectedRowInModel, 0);
            }
        
            if (this.actionType == AdminFrameConfig.ActionType.EDIT) {
                if (uuid != null) this.actEdition(uuid);
                else this.actEdition(longId);
            }
            else if (this.actionType ==  AdminFrameConfig.ActionType.DELETE) {
                if (uuid != null) this.actDeletion(uuid);
                else this.actDeletion(longId);
            }
        }
       
        isPushed = false;
        return button.getText();
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    private void actDeletion(UUID id) {
        int confirm = JOptionPane.showConfirmDialog(button, "Bạn chắc chắn xóa ID: " + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = this.parentPanel.actDeletion(id);

            if (success) {
                JOptionPane.showMessageDialog(button, "Xóa thành công!");
                this.parentPanel.reload();
            } else {
                JOptionPane.showMessageDialog(button, "Xóa thất bại!");
            }
        }
    }

    private void actEdition(UUID id) {
        this.parentPanel.actEdition(id);
    }

    private void actDeletion(Long id) {
        int confirm = JOptionPane.showConfirmDialog(button, "Bạn chắc chắn xóa ID: " + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = this.parentPanel.actDeletion(id);

            if (success) {
                JOptionPane.showMessageDialog(button, "Xóa thành công!");
                this.parentPanel.reload();
            } else {
                JOptionPane.showMessageDialog(button, "Xóa thất bại!");
            }
        }
    }

    private void actEdition(Long id) {
        this.parentPanel.actEdition(id);
    }
}
