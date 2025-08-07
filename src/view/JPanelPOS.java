/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view;

import dao.CustomerDAO;
import dao.OrderDAO;
import dao.OrderDetailDAO;
import dao.ProductDAO;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Customer;
import model.Order;
import model.OrderDetail;
import model.Product;
import utils.Auth;

/**
 *
 * @author Admin
 */
public class JPanelPOS extends javax.swing.JPanel {

    ProductDAO productDAO = new ProductDAO();
    CustomerDAO customerDAO = new CustomerDAO();
    OrderDAO orderDAO = new OrderDAO();
    OrderDetailDAO orderDetailDAO = new OrderDetailDAO();

    DefaultTableModel productTableModel;
    DefaultTableModel cartTableModel;
    DefaultTableModel customerTableModel;

    List<Product> productList;
    Customer currentCustomer = null;

    /**
     * Creates new form JPanelPOS
     */
    public JPanelPOS() {
        initComponents();
        init();
    }

    private void init() {
        productTableModel = (DefaultTableModel) jTable2.getModel();
        cartTableModel = (DefaultTableModel) jTable1.getModel();
        customerTableModel = (DefaultTableModel) jTable3.getModel();

        productTableModel.setColumnIdentifiers(new String[]{"Mã SKU", "Tên sản phẩm", "Tồn kho", "Giá bán"});
        cartTableModel.setColumnIdentifiers(new String[]{"Mã SKU", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"});
        customerTableModel.setColumnIdentifiers(new String[]{"Mã KH", "Tên khách hàng", "SĐT"});

        loadProductTable();
        loadCustomerTable();

        // Cập nhật lại text của nút cho đúng chức năng
        updatePendingOrderButton();
    }

    private void loadProductTable() {
        productTableModel.setRowCount(0);

        try {
            this.productList = productDAO.selectAll(); // Tải và lưu danh sách sản phẩm
            fillProductTable(this.productList);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách sản phẩm!");
        }
    }

    private void fillProductTable(List<Product> list) {
        productTableModel.setRowCount(0);
        for (Product p : list) {
            productTableModel.addRow(new Object[]{
                p.getSkuCode(),
                p.getProductName(),
                p.getStockQuantity(),
                p.getRetailPrice()
            });
        }
    }

    private void searchProducts() {
        String keyword = jTextField1.getText().trim().toLowerCase();
        List<Product> filteredList = this.productList.stream()
                .filter(p -> p.getProductName().toLowerCase().contains(keyword)
                || p.getSkuCode().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
        fillProductTable(filteredList);
    }

    private void addProductToCart() {
        int selectedRow = jTable2.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm để thêm vào giỏ hàng!");
            return;
        }

        String sku = (String) jTable2.getValueAt(selectedRow, 0);
        Product productToAdd = productDAO.selectBySkuCode(sku);

        // Kiểm tra tồn kho
        if (productToAdd.getStockQuantity() <= 0) {
            JOptionPane.showMessageDialog(this, "Sản phẩm đã hết hàng!");
            return;
        }

        // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            if (sku.equals(cartTableModel.getValueAt(i, 0))) {
                // Nếu có, tăng số lượng
                int currentQty = (int) cartTableModel.getValueAt(i, 2);
                cartTableModel.setValueAt(currentQty + 1, i, 2);
                updateCartLineTotal(i);
                return;
            }
        }

        // Nếu chưa có, thêm dòng mới
        cartTableModel.addRow(new Object[]{
            productToAdd.getSkuCode(),
            productToAdd.getProductName(),
            1, // Số lượng ban đầu
            productToAdd.getRetailPrice(),
            productToAdd.getRetailPrice() // Thành tiền ban đầu
        });
        updateTotalAmount();
    }

    private void updateCartLineTotal(int row) {
        int quantity = (int) cartTableModel.getValueAt(row, 2);
        BigDecimal price = (BigDecimal) cartTableModel.getValueAt(row, 3);
        BigDecimal lineTotal = price.multiply(new BigDecimal(quantity));
        cartTableModel.setValueAt(lineTotal, row, 4);
        updateTotalAmount();
    }

    private void updateTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            total = total.add((BigDecimal) cartTableModel.getValueAt(i, 4));
        }
        // Giả sử nút Thanh toán (jButton1) sẽ hiển thị tổng tiền
        DecimalFormat formatter = new DecimalFormat("###,###,### VND");
        jButton1.setText("Thanh toán: " + formatter.format(total));
    }

    private void loadCustomerTable() {
        customerTableModel.setRowCount(0);
        try {
            List<Customer> customers = customerDAO.selectAll();
            for (Customer c : customers) {
                customerTableModel.addRow(new Object[]{c.getCustomerId(), c.getFullName(), c.getPhoneNumber()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách khách hàng!");
        }
    }

    private void findAndSetCustomer() {
        String phone = jTextField2.getText().trim();
        if (phone.isEmpty()) {
            this.currentCustomer = null;
            // Cập nhật lại label thông tin khách hàng (nếu có)
            return;
        }
        try {
            Customer foundCustomer = customerDAO.selectByPhone(phone); // Cần viết hàm này trong CustomerDAO
            if (foundCustomer != null) {
                this.currentCustomer = foundCustomer;
                JOptionPane.showMessageDialog(this, "Đã chọn khách hàng: " + foundCustomer.getFullName());
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng. Hóa đơn sẽ được tạo cho khách vãng lai.");
                this.currentCustomer = null;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm khách hàng!");
        }
    }

    private void checkout() {
        if (cartTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng đang trống!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận thanh toán hóa đơn này?", "Xác nhận thanh toán", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // 1. Tạo Order
                Order order = new Order();
                order.setUserId(Auth.user.getUserId()); // Lấy ID nhân viên đăng nhập
                order.setCustomerId(currentCustomer != null ? currentCustomer.getCustomerId() : null);
                order.setStatus("Đã thanh toán");
                order.setPaymentMethod("Chờ xác nhận"); // Mặc định

                BigDecimal totalAmount = BigDecimal.ZERO;
                for (int i = 0; i < cartTableModel.getRowCount(); i++) {
                    totalAmount = totalAmount.add((BigDecimal) cartTableModel.getValueAt(i, 4));
                }
                order.setTotalAmount(totalAmount);

                int orderId = orderDAO.insertAndGetId(order);

                // 2. Tạo OrderDetails và cập nhật tồn kho
                for (int i = 0; i < cartTableModel.getRowCount(); i++) {
                    String sku = (String) cartTableModel.getValueAt(i, 0);
                    Product product = productDAO.selectBySkuCode(sku);
                    int quantity = (int) cartTableModel.getValueAt(i, 2);

                    OrderDetail detail = new OrderDetail();
                    detail.setOrderId(orderId);
                    detail.setProductId(product.getProductId());
                    detail.setQuantity(quantity);
                    detail.setPricePerUnit((BigDecimal) cartTableModel.getValueAt(i, 3));

                    orderDetailDAO.insert(detail);

                    // Cập nhật tồn kho (trừ đi số lượng đã bán)
                    product.setStockQuantity(product.getStockQuantity() - quantity);
                    productDAO.update(product);
                }

                JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
                resetPOS();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Thanh toán thất bại! Lỗi: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void resetPOS() {
        cartTableModel.setRowCount(0);
        currentCustomer = null;
        jTextField1.setText("");
        jTextField2.setText("");
        jButton1.setText("Thanh toán");
        loadProductTable(); // Tải lại danh sách sản phẩm để cập nhật tồn kho
    }

    private void luuHoaDonCho() {
        if (cartTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng đang trống, không có gì để lưu.");
            return;
        }

        try {
            Order pendingOrder = new Order();
            pendingOrder.setUserId(Auth.user.getUserId());
            pendingOrder.setCustomerId(currentCustomer != null ? currentCustomer.getCustomerId() : null);

            BigDecimal totalAmount = BigDecimal.ZERO;
            for (int i = 0; i < cartTableModel.getRowCount(); i++) {
                totalAmount = totalAmount.add((BigDecimal) cartTableModel.getValueAt(i, 4));
            }
            pendingOrder.setTotalAmount(totalAmount);
            pendingOrder.setStatus("Chờ xác nhận"); // Trạng thái quan trọng
            pendingOrder.setPaymentMethod(null); // Chưa có phương thức thanh toán

            int orderId = orderDAO.insertAndGetId(pendingOrder);

            for (int i = 0; i < cartTableModel.getRowCount(); i++) {
                OrderDetail detail = new OrderDetail();
                detail.setOrderId(orderId);
                String sku = (String) cartTableModel.getValueAt(i, 0);
                Product product = productDAO.selectBySkuCode(sku);
                detail.setProductId(product.getProductId());
                detail.setQuantity((int) cartTableModel.getValueAt(i, 2));
                detail.setPricePerUnit((BigDecimal) cartTableModel.getValueAt(i, 3));
                orderDetailDAO.insert(detail);
            }

            JOptionPane.showMessageDialog(this, "Đã lưu hóa đơn chờ thành công!");
            resetPOS();
            updatePendingOrderButton();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lưu hóa đơn chờ thất bại! Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void xemDanhSachHoaDonCho() {
        try {
            List<Order> pendingOrders = orderDAO.selectPendingOrders();
            if (pendingOrders.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có hóa đơn nào đang chờ.");
                return;
            }

            // Tạo một mảng các lựa chọn bao gồm tên hóa đơn và tùy chọn "Xóa..."
            Object[] options = new Object[pendingOrders.size() + 1];
            for (int i = 0; i < pendingOrders.size(); i++) {
                Order order = pendingOrders.get(i);
                options[i] = "Hóa đơn #" + order.getOrderId() + " - " + order.getTotalAmount() + " VND";
            }
            options[pendingOrders.size()] = "--- XÓA MỘT HÓA ĐƠN CHỜ ---";

            Object selected = JOptionPane.showInputDialog(
                    this,
                    "Chọn một hóa đơn để tải lại hoặc chọn để xóa:",
                    "Danh sách Hóa đơn chờ",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (selected != null) {
                if (selected.toString().contains("XÓA")) {
                    xoaHoaDonCho();
                } else {
                    int selectedOrderId = Integer.parseInt(selected.toString().split(" ")[1].replace("#", ""));
                    Order orderToLoad = pendingOrders.stream().filter(o -> o.getOrderId() == selectedOrderId).findFirst().orElse(null);
                    if (orderToLoad != null) {
                        taiHoaDonCho(orderToLoad);
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách hóa đơn chờ!");
            e.printStackTrace();
        }
    }

    private void taiHoaDonCho(Order orderToLoad) {
        if (cartTableModel.getRowCount() > 0) {
            int confirm = JOptionPane.showConfirmDialog(this, "Giỏ hàng hiện tại sẽ bị xóa. Bạn có muốn tiếp tục?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            List<OrderDetail> details = orderDetailDAO.selectByOrderId(orderToLoad.getOrderId());
            cartTableModel.setRowCount(0);

            for (OrderDetail detail : details) {
                Product product = productDAO.selectById(detail.getProductId());
                cartTableModel.addRow(new Object[]{
                    product.getSkuCode(),
                    product.getProductName(),
                    detail.getQuantity(),
                    detail.getPricePerUnit(),
                    detail.getPricePerUnit().multiply(new BigDecimal(detail.getQuantity()))
                });
            }

            if (orderToLoad.getCustomerId() != null) {
                this.currentCustomer = customerDAO.selectById(orderToLoad.getCustomerId());
            } else {
                this.currentCustomer = null;
            }

            updateTotalAmount();

            // Xóa hóa đơn chờ cũ khỏi CSDL sau khi đã tải thành công
            orderDAO.delete(orderToLoad.getOrderId());
            updatePendingOrderButton();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải hóa đơn chờ!");
            e.printStackTrace();
        }
    }

    private void xoaHoaDonCho() {
        List<Order> pendingOrders = orderDAO.selectPendingOrders();
        if (pendingOrders.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không còn hóa đơn nào để xóa.");
            return;
        }

        Object[] options = pendingOrders.stream()
                .map(order -> "Hóa đơn #" + order.getOrderId())
                .toArray();

        Object selected = JOptionPane.showInputDialog(
                this,
                "Chọn hóa đơn chờ cần xóa:",
                "Xóa Hóa đơn chờ",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                null);

        if (selected != null) {
            int selectedOrderId = Integer.parseInt(selected.toString().split(" ")[1].replace("#", ""));
            orderDAO.delete(selectedOrderId);
            updatePendingOrderButton();
            JOptionPane.showMessageDialog(this, "Đã xóa hóa đơn chờ thành công!");
        }
    }

    private void updatePendingOrderButton() {
        try {
            List<Order> pendingOrders = orderDAO.selectPendingOrders();
            jButton12.setText("Xem HĐ Chờ (" + pendingOrders.size() + ")");
        } catch (Exception e) {
            jButton12.setText("Xem HĐ Chờ (Lỗi)");
            e.printStackTrace();
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

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jTextField2 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(908, 523));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tên sản phẩm", "Giá", "Số lượng", "Thành tiền"
            }
        ));
        jTable1.setRowHeight(40);
        jScrollPane1.setViewportView(jTable1);

        jButton1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jButton1.setText("Thanh toán");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42))
        );

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel5.setText("Danh sách sản phẩm");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "STT", "Tên sản phẩm", "Màu sắc", "Kích thước", "Giá"
            }
        ));
        jTable2.setRowHeight(40);
        jScrollPane2.setViewportView(jTable2);
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(3).setHeaderValue("Kích thước");
            jTable2.getColumnModel().getColumn(4).setHeaderValue("Giá");
        }

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setText("Tìm kiếm sản phẩm");

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jButton2.setText("Thêm sản phẩm");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton10.setText("Tạo Hóa Đơn");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setText("Hóa Đơn Chờ");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setText("jButton10");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 32, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 506, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(291, 291, 291))
        );

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel7.setText("Danh sách khách hàng");

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Tên khách hàng", "Số điện thoại"
            }
        ));
        jTable3.setRowHeight(40);
        jScrollPane3.setViewportView(jTable3);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("Giỏ hàng");

        jButton3.setText("Xác nhận");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setText("Số điện thoại khách");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(16, 16, 16)
                    .addComponent(jLabel3)
                    .addContainerGap(190, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                        .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2)))
                .addContainerGap())
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(61, 61, 61)
                    .addComponent(jLabel3)
                    .addContainerGap(312, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 819, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 418, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        checkout();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        addProductToCart();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        findAndSetCustomer();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
        searchProducts();
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // TODO add your handling code here:
        resetPOS();

    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // TODO add your handling code here:
        luuHoaDonCho();

    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        // TODO add your handling code here:
        xemDanhSachHoaDonCho();

    }//GEN-LAST:event_jButton12ActionPerformed

    class HoaDonCho {

        private String tenHoaDon;
        private Vector<Vector> dataVector;
        private Customer khachHang;

        public HoaDonCho(String tenHoaDon, DefaultTableModel model, Customer khachHang) {
            this.tenHoaDon = tenHoaDon;
            this.dataVector = new Vector<>(model.getDataVector());
            this.khachHang = khachHang;
        }

        public Vector<Vector> getDataVector() {
            return dataVector;
        }

        public Customer getKhachHang() {
            return khachHang;
        }

        @Override
        public String toString() {
            return this.tenHoaDon; // Tên sẽ hiển thị trong danh sách lựa chọn
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
