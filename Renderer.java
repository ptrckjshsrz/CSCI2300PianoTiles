import javax.swing.JPanel;
import java.awt.Graphics;

@SuppressWarnings("serial")
public class Renderer extends JPanel {
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (PianoTile.dttwt != null) {
            PianoTile.dttwt.render(g);
        }
    }
}
