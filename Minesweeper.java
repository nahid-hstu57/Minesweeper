import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Minesweeper {

    private class MineTile extends JButton{
        int r;
        int c;
        public MineTile(int row, int col){
            this.r=row;
            this.c=col;
        }
    }

    int tilesize=70;
    int numrows=8;
    int numcols=8;
    int boardsize=tilesize*numrows;
    JFrame frame=new JFrame("Minesweeper");
    JLabel textLabel=new JLabel();
    JPanel textPanel=new JPanel();
    JPanel boardPanel=new JPanel();

    MineTile[][] mineTiles=new MineTile[numrows][numcols];

    ArrayList<MineTile>minelist;

    Minesweeper(){
       
        frame.setSize(boardsize,boardsize);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel.setFont(new Font("Arial",Font.BOLD,30));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Minesweeper");
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        frame.add(textPanel,BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(numrows,numcols));
        boardPanel.setBackground(Color.GRAY);
        frame.add(boardPanel);
        for(int r=0;r<numrows;r++){
            for(int c=0;c<numcols;c++)
            {
                MineTile tile = new MineTile(r, c);
                mineTiles [r][c]=tile;

                tile.setFocusable(false);
                tile.setMargin(new Insets(0,0,0,0));
                tile.setFont(new Font("Arial Unicode MS",Font.BOLD,45));
                // tile.setText("💣");
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e){
                        MineTile tile=(MineTile)e.getSource();

                        //left click
                        if(e.getButton()==MouseEvent.BUTTON1){
                            if(tile.getText().isEmpty()){
                                if(minelist.contains(tile))
                                {
                                    revealMine();
                                    textLabel.setText("Game Over!");
                                }
                            }
                        }
                    }
                    
                });

                boardPanel.add(tile);
            }
        }
         frame.setVisible(true);
         setMines();


    }

    void setMines()
    {
        minelist=new ArrayList<MineTile>();
        minelist.add(mineTiles[1][1]);
        minelist.add(mineTiles[2][2]);
        minelist.add(mineTiles[3][3]);  
        minelist.add(mineTiles[4][4]);
    }
    void revealMine()
    {
        for(int i=0;i<minelist.size();i++)
        {
            MineTile tile=minelist.get(i);
            tile.setText("💣");
        }
    }

    public static void main(String[] args) {
    new Minesweeper();
}
    
}
