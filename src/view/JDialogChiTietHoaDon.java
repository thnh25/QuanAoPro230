/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package view;

import dao.OrderDAO;
import dao.OrderDetailDAO;
import dto.OrderDetailDisplayDTO;
import dto.OrderDisplayDTO;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class JDialogChiTietHoaDon extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(JDialogChiTietHoaDon.class.getName());
    private int orderId;
    private OrderDAO orderDAO = new OrderDAO();
    private OrderDetailDAO detailDAO = new OrderDetailDAO();
    private OrderDisplayDTO currentOrder;
    /**
     * Creates new form JDialogChiTietHoaDon
     */
    public JDialogChiTietHoaDon(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    public JDialogChiTietHoaDon(java.awt.Frame parent, boolean modal, int orderId, OrderDisplayDTO orderInfo) {
        super(parent, modal);
        this.orderId = orderId;
        this.currentOrder = orderInfo;
        initComponents();
        init();
    }
    private void init() {
        setLocationRelativeTo(getParent());
        setTitle("Chi tiết Hóa đơn #" + orderId);
        
       
        lblMaHD.setText(String.valueOf(orderId));
        lblKhachHang.setText(currentOrder.getCustomerName());
        lblNhanVien.setText(currentOrder.getStaffName());
        
       
        String[] allStatuses = {"Chờ xác nhận", "Đang chuẩn bị hàng", "Đang giao hàng", "Hoàn thành", "Đã hủy"};
        DefaultComboBoxModel<String> statusModel = new DefaultComboBoxModel<>(allStatuses);
        cboTrangThai.setModel(statusModel);
        
       
        cboTrangThai.setSelectedItem(currentOrder.getStatus());
        
        
        if ("Hoàn thành".equalsIgnoreCase(currentOrder.getStatus()) || "Đã hủy".equalsIgnoreCase(currentOrder.getStatus())) {
            cboTrangThai.setEnabled(false);
            btnLuuTrangThai.setEnabled(false);
        }
        
       
        DefaultTableModel tableModel = (DefaultTableModel) tblChiTiet.getModel();
        tableModel.setColumnIdentifiers(new String[]{"Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"});
        loadTableData();
    }
    
    private void loadTableData() {
        DefaultTableModel model = (DefaultTableModel) tblChiTiet.getModel();
        model.setRowCount(0);
        DecimalFormat moneyFormatter = new DecimalFormat("###,###,### VND");
        
        List<OrderDetailDisplayDTO> list = detailDAO.selectDetailsForDisplay(orderId);
        for (OrderDetailDisplayDTO dto : list) {
            model.addRow(new Object[]{
                dto.getSkuCode(),
                dto.getProductName(),
                dto.getQuantity(),
                moneyFormatter.format(dto.getPricePerUnit()),
                moneyFormatter.format(dto.getLineTotal())
            });
        }
    }
    
   
    private void saveStatus() {
        String newStatus = (String) cboTrangThai.getSelectedItem();
        String oldStatus = currentOrder.getStatus();
        
       
        if (newStatus.equalsIgnoreCase(oldStatus)) {
            JOptionPane.showMessageDialog(this, "Trạng thái không có gì thay đổi.");
            return;
        }

       
        List<String> statusFlow = Arrays.asList("Chờ xác nhận", "Đang chuẩn bị hàng", "Đang giao hàng", "Hoàn thành");
        int oldIndex = statusFlow.indexOf(oldStatus);
        int newIndex = statusFlow.indexOf(newStatus);

        
        if (newStatus.equalsIgnoreCase("Đã hủy")) {
          
        } 
      
        else if (oldIndex != -1 && newIndex != -1 && newIndex < oldIndex) {
            JOptionPane.showMessageDialog(this, "Không thể quay lại trạng thái trước đó!", "Lỗi logic", JOptionPane.ERROR_MESSAGE);
            cboTrangThai.setSelectedItem(oldStatus); 
            return;
        }
       
        else if (oldIndex != -1 && newIndex != -1 && (newIndex - oldIndex > 1)) {
             JOptionPane.showMessageDialog(this, "Vui lòng chuyển qua từng trạng thái theo đúng quy trình!", "Lỗi logic", JOptionPane.ERROR_MESSAGE);
             cboTrangThai.setSelectedItem(oldStatus);
             return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn đổi trạng thái hóa đơn thành '" + newStatus + "' không?", 
            "Xác nhận thay đổi", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                orderDAO.updateStatus(orderId, newStatus);
                JOptionPane.showMessageDialog(this, "Cập nhật trạng thái thành công!");
                this.dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Cập nhật trạng thái thất bại!");
                e.printStackTrace();
            }
        } else {
           
            cboTrangThai.setSelectedItem(oldStatus);
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblMaHD = new javax.swing.JTextField();
        lblKhachHang = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblNhanVien = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblChiTiet = new javax.swing.JTable();
        btnLuuTrangThai = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        cboTrangThai = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Chi tiết Hóa đơn");

        jLabel2.setText("Mã HĐ");

        jLabel3.setText("Tên khách");

        jLabel4.setText("Trạng thái");

        jLabel5.setText("Nhân viên");

        tblChiTiet.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblChiTiet);

        btnLuuTrangThai.setText("Lưu trạng thái");
        btnLuuTrangThai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLuuTrangThaiActionPerformed(evt);
            }
        });

        jButton2.setText("Thoát");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        cboTrangThai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnLuuTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(130, 130, 130)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(jLabel2)
                                    .addGap(199, 199, 199)
                                    .addComponent(jLabel3)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(lblKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblMaHD, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addGap(18, 18, 18)
                                        .addComponent(lblNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel2))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3)
                                    .addComponent(lblMaHD, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(lblKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel5))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)
                        .addComponent(cboTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLuuTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLuuTrangThaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLuuTrangThaiActionPerformed
        // TODO add your handling code here:
        saveStatus();
    }//GEN-LAST:event_btnLuuTrangThaiActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JDialogChiTietHoaDon dialog = new JDialogChiTietHoaDon(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLuuTrangThai;
    private javax.swing.JComboBox<String> cboTrangThai;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField lblKhachHang;
    private javax.swing.JTextField lblMaHD;
    private javax.swing.JTextField lblNhanVien;
    private javax.swing.JTable tblChiTiet;
    // End of variables declaration//GEN-END:variables
}
