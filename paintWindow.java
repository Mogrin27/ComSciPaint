package compSciPaint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Jodie Wilbur
 * @since 4/27/2021
 * Creates a JFrame and JButtons as well as handles all the mouse input and shapes to be drawn.
 */

public class paintWindow extends JFrame {

    /**
     * Initializes the buttons to be used in the program.
     */
    JButton brushButton, lineButton, ellipseButton, recButton, paintButton, fillButton;

    /**
     * Sets the default currentAction so when the program starts you can start painting.
     */
    int currentAction = 1;

    /**
     * Sets the default colors to black.
     */
    Color strokeColor = Color.BLACK, fillColor = Color.BLACK;

    /**
     * paintWindow draws the JFrame window and creates/populates a toolbar so you can change shapes to draw with.
     */
    paintWindow() {
        /**
         * Creates amd sets default vales for the JFrame.
         */
        this.setTitle("CSPaint");
        this.setSize(600, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBackground(Color.WHITE);
        JToolBar toolBar = new JToolBar();
        toolBar.setRollover(true);

        /**
         * Create and name buttons and give them an assigned value.
         */
        brushButton = makeButton("Brush", 1);
        lineButton = makeButton("Line", 2);
        ellipseButton = makeButton("Ellipse", 3);
        recButton = makeButton("Rectangle", 4);

        /**
         * Create and name buttons used for color
         */
        paintButton = makeColorButton("Outline Color Chooser", 5, true);
        fillButton = makeColorButton("Fill Color Chooser", 6, false);

        /**
         * Add buttons to the box
         */
        toolBar.add(brushButton);
        toolBar.add(lineButton);
        toolBar.add(ellipseButton);
        toolBar.add(recButton);
        toolBar.add(paintButton);
        toolBar.add(fillButton);
        
        /**
         * This adds the toolbar and aligns it to the top of the screen.
         * It also creates the DrawingPlain witch takes mouse input.
         */
        this.add(toolBar, BorderLayout.NORTH);
        this.add(new DrawingPlain(), BorderLayout.CENTER);
        this.setVisible(true);

    }

    /**
     * Method for creating buttons and giving them a value
     * @param brushAction An integer that is used to change what shape you want to draw.
     * @param name The name to display ontop of the button
     * @return This returns the button so it can be used.
     */
    public JButton makeButton(String name, final int brushAction){
        JButton buttons = new JButton();
        buttons.setText(name);
        buttons.setVisible(true);

        //TODO add icons to buttons

        buttons.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentAction = brushAction; //This stores the button integer
                System.out.println("brushAction: " + brushAction); //This will print out "brushAction: " and the bush action integer for debugging.
            }
        });
        return buttons;
    }

    /**
     * This will create the buttons that bring up the color chooser window.
     * @param name The name to display on the button
     * @param actionNumber An integer that is used to bring up the proper color chooser window.
     * @param stroke This is a boolean value. True will change the outline color (strokeColor), false is for the inside of the shape (fillColor).
     * @return Returns the button so it can be added to the window.
     */
    public JButton makeColorButton(String name, final int actionNumber, final boolean stroke){
        JButton buttons = new JButton();
        buttons.setText(name);
        buttons.setVisible(true);

        //TODO add icons to buttons

        buttons.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(stroke){
                    strokeColor = JColorChooser.showDialog(null, "Pick an outline color.", Color.BLACK); //This is for the outline color
                }else {
                    fillColor = JColorChooser.showDialog(null, "Pick fill color.", Color.BLACK); //This is for shape fill color
                }
            }
        });
        return buttons;
    }

    /**
     * This is where all the mouse input happens
     * We create ArrayLists to store
     */
    private class DrawingPlain extends JComponent {
        ArrayList<Shape> shapes = new ArrayList<Shape>();
        ArrayList<Color> shapeStrokes = new ArrayList<Color>();
        ArrayList<Color> shapeFill = new ArrayList<Color>();
        Point pointStart, pointEnd;

        /**
         * This method creates the events to track mouse input.
         */
        public DrawingPlain() {
            this.addMouseListener(new MouseAdapter() {

                /**
                 *
                 * @param e Used to grab the starting x and y coordinates.
                 */
                public void mousePressed(MouseEvent e) {
                    pointStart = new Point(e.getX(), e.getY());
                    pointEnd = pointStart;
                    repaint();
                }

                /**
                 * This Method will grab the starting and ending x y values.
                 * It also gets the proper shape based on currentAction integer, and will also get color values.
                 * The repaint() is what draws the shape onto the board so it will stay when mouse is released.
                 * @param e Used to get the ending x y values from the mouse.
                 */
                public void mouseReleased(MouseEvent e) {
                    if(currentAction == 2){
                        Shape shape = drawLine(pointStart.x, pointStart.y, e.getX(), e.getY());
                        shapes.add(shape);
                        shapeFill.add(fillColor);
                        shapeStrokes.add(strokeColor);

                        pointStart = null;
                        pointEnd = null;
                        repaint();

                        System.out.println("drawLine: " + "currentAction " + currentAction + " " + shape); //This prints out information used for this mouse event
                    }else if(currentAction == 3){
                        Shape shape = drawEllipse(pointStart.x, pointStart.y, e.getX(), e.getY());
                        shapes.add(shape);
                        shapeFill.add(fillColor);
                        shapeStrokes.add(strokeColor);

                        pointStart = null;
                        pointEnd = null;
                        repaint();

                        System.out.println("drawEllipse: " + "currentAction " + currentAction + " " + shape); //This prints out information used for this mouse event
                    }else if(currentAction == 4) {
                        Shape shape = drawRectangle(pointStart.x, pointStart.y, e.getX(), e.getY());
                        shapes.add(shape);
                        shapeFill.add(fillColor);
                        shapeStrokes.add(strokeColor);

                        pointStart = null;
                        pointEnd = null;
                        repaint();

                        System.out.println("drawRectangle: " + "currentAction " + currentAction + " " + shape); //This prints out information used for this mouse event
                    }
                }
            });

            /**
             * The mouseDragged tracks your mouse once you click and drag the pointer across the window.
             * The currentAction == 1 will draw small ovals on the screen as you drag.
             * All the ovals are the same size and are printed over and over again.
             */
            this.addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    if (currentAction == 1) {
                        Shape shape = drawEllipse(e.getX(), e.getY(), e.getX()+1, e.getY()+1);
                        shapes.add(shape);
                        shapeFill.add(fillColor);
                        shapeStrokes.add(strokeColor);

                        pointStart = null;
                        pointEnd = null;
                        repaint();

                        System.out.println("drawEllipse: " + "currentAction " + currentAction + " " + shape); //This prints out information used for this mouse event
                    } else if (currentAction > 1) {
                        pointEnd = new Point(e.getX(), e.getY());
                        repaint();
                    }
                }
            });
        }

        /**
         * This is where most of the graphics setting are created and where the color arrays are iterated.
         * The RenderingHints.KEY_ANTIALIASING handles cleaning up the edges of the shapes so they appear smother.
         * The AlphaComposite.SRC_OVER handles transparency of the shapes that are drawn.
         * The 1.0f set the shapes to be a solid color. Lowering it will make the shapes see through.
         * @param g An input variable used with swing Graphics.
         */
        public void paint(Graphics g) {
            Graphics2D graphicsSetting = (Graphics2D) g;
            graphicsSetting.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphicsSetting.setStroke(new BasicStroke(2)); //Sets the default stroke width to 2
            graphicsSetting.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

            //This is where the color arrays are read from. These store a RGB value.
            Iterator<Color> strokeCounter = shapeStrokes.iterator();
            Iterator<Color> fillCounter = shapeFill.iterator();
            System.out.println(shapeStrokes);
            for (Shape i : shapes) {
                graphicsSetting.setPaint(strokeCounter.next());
                graphicsSetting.draw(i);
                graphicsSetting.setPaint(fillCounter.next());
                graphicsSetting.fill(i);
            }

            /**
             * This if statement checks to see if the pointStart and pointEnd contain an x y value.
             * the current action number is attached to the buttons we created previously and this provides an outline shape.
             * The graphicsSetting.draw(shape) does not paint the permanent shape onto the board but rather give you a visual of the shape and where it will be drawn
             */
            if (pointStart != null && pointEnd != null) {
//                graphicsSetting.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f)); //Uncomment this to make the guide shape transparent.
                if(currentAction == 2){
                    Shape shape = drawLine(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y);
                    graphicsSetting.draw(shape); //draw(shape) draws the shape selected as an outline and will not leave it on the window
                }else if(currentAction == 3){
                    Shape shape = drawEllipse(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y);
                    graphicsSetting.draw(shape);//draw(shape) draws the shape selected as an outline and will not leave it on the window
                }else if(currentAction == 4) {
                    Shape shape = drawRectangle(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y);
                    graphicsSetting.draw(shape);//draw(shape) draws the shape selected as an outline and will not leave it on the window
                }
            }
        }

        /**
         * This is a method used to draw a line on the window.
         * @param xStart Starting x coordinate.
         * @param yStart Starting y coordinate.
         * @param xEnd Ending x coordinate.
         * @param yEnd Ending y coordinate.
         * @return This returns a new Line2D object so it can be drawn.
         */
        private Line2D.Float drawLine(int xStart, int yStart, int xEnd, int yEnd) {
            return new Line2D.Float(xStart, yStart, xEnd, yEnd);
        }

        /**
         * This is a method used to draw a line on the window.
         * @param xStart Starting x coordinate.
         * @param yStart Starting y coordinate.
         * @param xEnd Ending x coordinate.
         * @param yEnd Ending y coordinate.
         * @return This returns a new Rectangle2D object so it can be drawn.
         */
        private Rectangle2D.Float drawRectangle(int xStart, int yStart, int xEnd, int yEnd) {
            int x = Math.min(xStart, xEnd);
            int y = Math.min(yStart, yEnd);

            int width = Math.abs(xStart - xEnd);
            int height = Math.abs(yStart - yEnd);

            return new Rectangle2D.Float(x, y, width, height);
        }

        /**
         * This is a method used to draw a line on the window.
         * @param xStart Starting x coordinate.
         * @param yStart Starting y coordinate.
         * @param xEnd Ending x coordinate.
         * @param yEnd Ending y coordinate.
         * @return This returns a new Ellipse2D object so it can be drawn.
         */
        private Ellipse2D.Float drawEllipse(int xStart, int yStart, int xEnd, int yEnd) {
            int x = Math.min(xStart, xEnd);
            int y = Math.min(yStart, yEnd);

            int width = Math.abs(xStart - xEnd);
            int height = Math.abs(yStart - yEnd);

            return new Ellipse2D.Float(x, y, width, height);
        }
    }
}