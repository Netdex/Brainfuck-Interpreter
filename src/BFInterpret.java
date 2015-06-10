import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;
import javax.swing.UIManager;


public class BFInterpret {

	private static final int MEMORY_SIZE = 500;
	private static final long CPU_DELAY = 0;
	private static final boolean DO_VISUALIZATIONS = true;
	
	private static File file = new File("mandelbrot.b");
	private static InputStreamReader std_in;
	
	public static char[] inst;
	public static char[] memory;
	public static int iptr;
	public static int ptr;
	
	public static BFPanel bfp;
	public static Timer stepTimer;
	
	public static void main(String[] args) throws Exception {
		std_in = new InputStreamReader(System.in);
		BufferedReader f_in = new BufferedReader(new FileReader(file));
		
		String ucode = "";
		String line;
		while((line = f_in.readLine()) != null){
			ucode += line;
		}
		ucode = ucode.replaceAll("[^><\\+-\\.,\\[\\]]", "");
		inst = ucode.toCharArray();
		f_in.close();
		
		memory = new char[MEMORY_SIZE];
		ptr = 0;
		
		stepTimer = new Timer((int) CPU_DELAY, new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				if(iptr < inst.length)
					step();
				else
					stepTimer.stop();
			}
		});
		if(DO_VISUALIZATIONS){
			new Thread(){
				public void run(){
					try{
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
						JFrame viz = new JFrame("Brainfuck Memory Visualizer");
						viz.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
						viz.setLayout(new BorderLayout());
						viz.setResizable(false);
						
						JButton btnStart = new JButton("Inf Step/Stop");
						JButton btnStep = new JButton("Step");
						JLabel lblDebug = new JLabel("Awaiting start");
						viz.add(btnStart, BorderLayout.SOUTH);
						viz.add(btnStep, BorderLayout.EAST);
						viz.add(lblDebug, BorderLayout.NORTH);
						
						btnStart.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent event){
								if(stepTimer.isRunning())
									stepTimer.stop();
								else{
									new Thread(){
										@Override
										public void run(){
											while(true){
												try{
													step();
													Thread.sleep(CPU_DELAY);
												}catch(Exception e){
													
												}
											}
										}
									}.start();
								}
							}
						});
						btnStep.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent event){
								step();
							}
						});
						bfp = new BFPanel(1500, 500, memory, lblDebug);
						viz.add(bfp, BorderLayout.CENTER);
						
						viz.pack();
						viz.setVisible(true);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}.start();
		}
	}
	
	public static void step() {
		try{
			char c = inst[iptr];
			switch(c){
			case '>':
				ptr++;
				break;
			case '<':
				ptr--;
				break;
			case '+':
				memory[ptr]++;
				break;
			case '-':
				memory[ptr]--;
				break;
			case '.':
				System.out.print((char) memory[ptr]);
				break;
			case ',':
				memory[ptr] = (char) std_in.read();
				break;
			case '[':
				if(memory[ptr] == 0){
					int b = 1;
					do{
						iptr++;
						if		(inst[iptr] == '[')	b++;
						else if	(inst[iptr] == ']')	b--;
					}while(b != 0);
				}
				break;
			case ']':
				if(memory[ptr] != 0){
					int b = 0;
					do {
						if		(inst[iptr] == '[') 	b++;
						else if	(inst[iptr] == ']') 	b--;
						iptr--;
					} while(b != 0);
				}
				break;
			}
			if		(ptr < 0)				ptr = MEMORY_SIZE - 1;
			else if	(ptr > MEMORY_SIZE - 1)	ptr = 0;
		}catch(Exception e){
			
		}
		if(DO_VISUALIZATIONS){
			bfp.refresh();
		}
		iptr++;
	}
}
