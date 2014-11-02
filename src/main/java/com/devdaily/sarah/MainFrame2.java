package com.devdaily.sarah;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.MouseInputListener;

/**
 * Original code at http://marioyohanes.com/2008/10/31/translucent-custom-shape-window/
 */
public class MainFrame2 extends JFrame {

    private JComponent titlePane, contentPane, bottomPane;
    private JLabel titleLabel, resizeLabel;
    private JButton closeButton;
    private Window w = this;
    private ActionListener closeListener;
    private JTextField textField;

    // handle 'minimize'
    JMenuBar menuBar = new JMenuBar();
    JMenu windowMenu = new JMenu();
    JMenuItem minimizeWindowMenuItem = new JMenuItem();

    public MainFrame2(String title) {
        setUndecorated(true);
        setBackground(new Color(0,0,0,0));
        setContentPane(createContentPane());
        configureComponents();
        configureMenu();
        setSize(700, 62);
    }
    
    private void configureMenu() {
        windowMenu.setText("Window");
        minimizeWindowMenuItem.setText("Minimize");
        minimizeWindowMenuItem.addActionListener(new ActionListener()
        {
          public void actionPerformed(final ActionEvent e)
          {
            minimizeWindowMenuItem_actionPerformed(e);
          }
        });
        minimizeWindowMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        windowMenu.add(minimizeWindowMenuItem);
        menuBar.add(windowMenu);
        this.setJMenuBar(menuBar);       
    }

    // TODO move this to the controller
    public void minimizeWindowMenuItem_actionPerformed(final ActionEvent e) {
      this.setExtendedState(Frame.ICONIFIED);
    }
    
    public JTextField getTextField() {
        return this.textField;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setTitle(String title){
        titleLabel.setText(title);
    }

    /**
     * Install window closing listener when close button pressed.
     * @param listener desired action to take.
     */
    public void installCloseButtonListener(ActionListener listener){
        if(closeListener != null){
            closeButton.removeActionListener(closeListener);
        }

        closeListener = listener;
        closeButton.addActionListener(closeListener);
    }

    private void configureComponents(){
        textField = new JTextField();
        textField.setFont(textField.getFont().deriveFont(24.0f));
        textField.setText("");
        textField.setColumns(42);
        textField.setBounds(20,20,textField.getHeight(),textField.getWidth());
        textField.setBackground(new Color(230,230,230));
        titleLabel = new JLabel(getTitle());
        titleLabel.setForeground(Color.WHITE);
        closeButton = new JButton();
        closeButton.setFocusable(false);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        titlePane = createTitlePane();
        //resizeLabel = new JLabel(new ImageIcon(MainFrame2.class.getResource(
        //  "resize_corner_dark.png")));
        bottomPane = createBottomPane();

        setLayout(new BorderLayout());
        add(titlePane, BorderLayout.CENTER);
        //add(contentPane, BorderLayout.CENTER);
        add(bottomPane, BorderLayout.SOUTH);

        MouseInputHandler handler = new MouseInputHandler();
        titlePane.addMouseListener(handler);
        titlePane.addMouseMotionListener(handler);
    }

    public JComponent createBottomPane(){
        JComponent result = new JComponent(){};
        result.setBackground(Color.BLACK);
        result.setLayout(new FlowLayout(FlowLayout.RIGHT));
        //result.add(resizeLabel);
        return result;
    }

    public JComponent createTitlePane() {
        JComponent result = new JComponent(){
            protected void paintComponent(Graphics g){
                setOpaque(false);
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Composite old = g2.getComposite();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
                Shape shape = new RoundRectangle2D.Float(0,0,getWidth(), getHeight(), 20, 20);
                g2.fill(shape);
                g2.setComposite(old);
                g2.dispose();
            }
        };

        GroupLayout layout = new GroupLayout(result);
        result.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                .addGap(8)
                .addComponent(textField)
                //.addComponent(closeButton)
                .addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                //.addComponent(titleLabel)
                .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(8)
                )
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                .addGap(8)
                .addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(textField)
                                //.addComponent(titleLabel)
                                //.addComponent(closeButton)
                        )
                )
        );

        return result;
    }

    public JComponent createContentPane() {
        return new JComponent(){
            @Override
            protected void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Composite old = g2.getComposite();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
                g2.setColor(new Color(0,0,0));
                Shape shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16);
                g2.fill(shape);
                g2.setComposite(old);
                g2.dispose();
            }
        };
    }

    /**
     * Class handling mouse input to enable titlePane become drag-able and window become move-able.
     */
    private class MouseInputHandler implements MouseInputListener {
        private boolean isMovingWindow;
        private int dragOffsetX;
        private int dragOffsetY;
        private static final int BORDER_DRAG_THICKNESS = 5;

        public void mousePressed(MouseEvent ev) {
            Point dragWindowOffset = ev.getPoint();
            if (w != null) {
                w.toFront();
            }
            Point convertedDragWindowOffset = SwingUtilities.convertPoint(
                           w, dragWindowOffset, titlePane);

            Frame f = null;
            Dialog d = null;

            if (w instanceof Frame) {
                f = (Frame)w;
            } else if (w instanceof Dialog) {
                d = (Dialog)w;
            }

            int frameState = (f != null) ? f.getExtendedState() : 0;

            if (titlePane.contains(convertedDragWindowOffset)) {
                if ((f != null && ((frameState & Frame.MAXIMIZED_BOTH) == 0)
                        || (d != null))
                        && dragWindowOffset.y >= BORDER_DRAG_THICKNESS
                        && dragWindowOffset.x >= BORDER_DRAG_THICKNESS
                        && dragWindowOffset.x < w.getWidth()
                            - BORDER_DRAG_THICKNESS) {
                    isMovingWindow = true;
                    dragOffsetX = dragWindowOffset.x;
                    dragOffsetY = dragWindowOffset.y;
                }
            }
            else if (f != null && f.isResizable()
                    && ((frameState & Frame.MAXIMIZED_BOTH) == 0)
                    || (d != null && d.isResizable())) {
                dragOffsetX = dragWindowOffset.x;
                dragOffsetY = dragWindowOffset.y;
            }
        }

        public void mouseReleased(MouseEvent ev) {
            isMovingWindow = false;
        }

        public void mouseDragged(MouseEvent ev) {
            if (isMovingWindow) {
                Point windowPt = MouseInfo.getPointerInfo().getLocation();
                windowPt.x = windowPt.x - dragOffsetX;
                windowPt.y = windowPt.y - dragOffsetY;
                w.setLocation(windowPt);
            }
        }

        public void mouseClicked(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
        public void mouseMoved(MouseEvent e) {}
    }

}


