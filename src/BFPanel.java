import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class BFPanel extends JPanel {

	private char[] memory;
	private JLabel lblDebug;
	
	private final int rows = 10;

	public BFPanel(int width, int height, char[] memory, JLabel lblDebug) {
		this.memory = memory;
		this.lblDebug = lblDebug;
		
		this.setPreferredSize(new Dimension(width, height));
	}

	public void refresh() {
		if(BFInterpret.iptr < BFInterpret.inst.length){
			char cinst = BFInterpret.inst[BFInterpret.iptr];
			char ninst;
			if(BFInterpret.iptr < BFInterpret.inst.length - 1)
				ninst = BFInterpret.inst[BFInterpret.iptr + 1];
			else
				ninst = 'N';
			lblDebug.setText(String.format("iptr: %s | cinst: %s | ninst: %s | ptr: %s", BFInterpret.iptr, cinst, ninst, BFInterpret.ptr));
		}
		else{
			lblDebug.setText("Execution Complete");
		}
		
		repaint();
	}

	@Override
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;

		Font font1 = new Font("Arial", Font.PLAIN, 10);
		Font font2 = new Font("Arial", Font.PLAIN, 20);
		
		g.setFont(font2);
		// g.drawString(BFInterpret.inst[BFInterpret.iptr] + "", 10, 20);
		int pixelX = this.getWidth() / memory.length * rows;
		int pixelY = this.getHeight() / rows;
		for (int i = 0; i < memory.length; i++) {
			int px = i % (memory.length / rows) * pixelX;
			int py = (i / (memory.length / rows)) * pixelY;
			if (i == BFInterpret.ptr) {
				g.setColor(Color.PINK);
				g.fillRect(px, py, pixelX, this.getHeight() / rows);
				g.setColor(Color.BLACK);
			}
			g.drawRect(px, py, pixelX, this.getHeight() / rows);
			g.setFont(font1);
			String t1 = (int) memory[i] + "";
			g.drawString(t1, px + 3, py + g.getFontMetrics().getAscent());
			g.setFont(font2);
			String t2 = (char) memory[i] + "";
			g.drawString(t2,
					px + pixelX / 2 - g.getFontMetrics().stringWidth(t2) / 2,
					py + pixelY / 2 + g.getFontMetrics().getAscent() / 2);
		}
	}
}
