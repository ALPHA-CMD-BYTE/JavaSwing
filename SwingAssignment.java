import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;

/**
 * Java Swing First Individual Assignment
 * Demonstrates JFrame, JPanel, JLabel, ImageIcon, inheritance and constructors.
 * Addition: "Change Text" lets the user type new text for the JLabel and
 * "Change Colours" lets the user choose any colour for the JPanel background.
 */

// Inheritance: AppPanel extends JPanel (parent constructor called with super)
class AppPanel extends JPanel {

    // Constructor
    public AppPanel(Color background) {
        super(new BorderLayout());
        setBackground(background);
        setBorder(new EmptyBorder(30, 30, 30, 30));
    }
}

// Inheritance: RoundButton extends JButton and draws a rounded, modern button
class RoundButton extends JButton {

    // Constructor
    public RoundButton(String text) {
        super(text);
        setFont(new Font("Tahoma", Font.BOLD, 15));
        setForeground(new Color(15, 23, 42));
        setPreferredSize(new Dimension(160, 46));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color base = Color.WHITE;
        g2.setColor(getModel().isPressed() ? new Color(226, 232, 240)
                : getModel().isRollover() ? new Color(241, 245, 249) : base);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
        g2.setColor(new Color(0, 0, 0, 50));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
        g2.dispose();
        super.paintComponent(g); // draws the text
    }
}

// Draws an ImageIcon inside a circle with a ring around it
class RoundIcon implements Icon {
    private final Image image;
    private final int size;

    // Constructor
    public RoundIcon(ImageIcon source, int size) {
        this.image = source.getImage();
        this.size = size;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        int pad = 6;
        g2.setColor(Color.WHITE);
        g2.fill(new Ellipse2D.Double(x, y, size, size));
        g2.setClip(new Ellipse2D.Double(x + pad, y + pad, size - 2 * pad, size - 2 * pad));
        g2.drawImage(image, x + pad, y + pad, size - 2 * pad, size - 2 * pad, null);
        g2.setClip(null);
        g2.setColor(new Color(0, 0, 0, 60));
        g2.setStroke(new BasicStroke(2f));
        g2.draw(new Ellipse2D.Double(x + 1, y + 1, size - 2, size - 2));
        g2.dispose();
    }

    @Override public int getIconWidth()  { return size; }
    @Override public int getIconHeight() { return size; }
}

public class SwingAssignment {

    // JFrame variable named frame
    private JFrame frame = new JFrame();

    // JPanel variable named panel (instance of the subclass AppPanel)
    private JPanel panel = new AppPanel(new Color(224, 242, 254));

    // JLabel variable named label
    private JLabel label = new JLabel("", SwingConstants.CENTER);

    private String currentText = "Welcome to Java Swing First Individual Assignment by Osman Sheriff";
    private Timer fadeTimer;

    // Constructor
    public SwingAssignment() {
        // Frame customisation
        frame.setTitle("Java Swing Assignment");
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // closes app on X

        // ImageIcon: application icon + round icon shown above the label
        ImageIcon icon = new ImageIcon("icon.png");
        frame.setIconImage(icon.getImage());
        label.setIcon(new RoundIcon(icon, 150));
        label.setVerticalTextPosition(SwingConstants.BOTTOM);
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        label.setIconTextGap(24);
        label.setFont(new Font("Tahoma", Font.BOLD, 24));
        showText(currentText);
        updateLabelColor(panel.getBackground());

        // Add label to panel
        panel.add(label, BorderLayout.CENTER);

        // Addition: two buttons (Change Text, Colors)
        RoundButton textButton = new RoundButton("Change Text");
        RoundButton colorButton = new RoundButton("Change Colour");

        // Change Text: asks the user to type the new text
        textButton.addActionListener((ActionEvent e) -> {
            String input = (String) JOptionPane.showInputDialog(frame,
                    "Type the new text:", "Change Text",
                    JOptionPane.PLAIN_MESSAGE, null, null, currentText);
            if (input != null && !input.trim().isEmpty()) {
                currentText = input.trim();
                showText(currentText);
            }
        });

        // Colors: opens the colour chooser so the user can pick any colour
        colorButton.addActionListener((ActionEvent e) -> {
            Color picked = JColorChooser.showDialog(frame, "Choose background colour",
                    panel.getBackground());
            if (picked != null) fadeTo(picked);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.add(textButton);
        buttonPanel.add(colorButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Add panel to frame
        frame.add(panel);
        frame.setVisible(true);
    }

    // Sets the label text, wrapping long text onto several centred lines
    private void showText(String text) {
        String safe = text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        label.setText("<html><div style='text-align:center; width:560px'>" + safe + "</div></html>");
    }

    // Smoothly fades the panel background to a new colour
    private void fadeTo(Color target) {
        if (fadeTimer != null && fadeTimer.isRunning()) fadeTimer.stop();
        final Color start = panel.getBackground();
        final int steps = 12;
        final int[] step = {0};
        fadeTimer = new Timer(20, null);
        fadeTimer.addActionListener((ActionEvent e) -> {
            step[0]++;
            float t = step[0] / (float) steps;
            Color c = new Color(
                    (int) (start.getRed()   + (target.getRed()   - start.getRed())   * t),
                    (int) (start.getGreen() + (target.getGreen() - start.getGreen()) * t),
                    (int) (start.getBlue()  + (target.getBlue()  - start.getBlue())  * t));
            panel.setBackground(c);
            updateLabelColor(c);
            if (step[0] >= steps) fadeTimer.stop();
        });
        fadeTimer.start();
    }

    // Keeps the label readable: dark text on light colours, white on dark
    private void updateLabelColor(Color bg) {
        double luminance = (0.299 * bg.getRed() + 0.587 * bg.getGreen() + 0.114 * bg.getBlue()) / 255;
        label.setForeground(luminance > 0.55 ? new Color(15, 23, 42) : Color.WHITE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SwingAssignment::new);
    }
}
