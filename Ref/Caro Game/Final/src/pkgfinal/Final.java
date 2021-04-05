package pkgfinal;

import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

public class Final {   
    
    public static JFrame f;
    JButton[][] bt;
    static boolean flat = false;
    boolean winner;
    JButton send;
    Timer thoigian;
    String temp="",strNhan = "";
    Integer second, minute;
    JLabel demthoigian;
    JTextArea content;
    JTextField nhap,enterchat;
    JPanel p;
    int xx, yy, x, y;
    int[][] matran;
    int[][] matrandanh;
    
    // Server Socket
    ServerSocket serversocket;
    Socket socket;
    OutputStream os;// ....
    InputStream is;// ......
    ObjectOutputStream oos;// .........
    ObjectInputStream ois;// 
    
    //MenuBar
    MenuBar menubar;
    
    public Final() {
        f = new JFrame();
        f.setTitle("Game Caro");
        f.setSize(750, 500);
        x = 25;
        y = 25;
        f.getContentPane().setLayout(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        f.setResizable(false);
        
        matran = new int[x][y];
        matrandanh = new int[x][y];
        menubar = new MenuBar();
        p = new JPanel();
        p.setBounds(10, 30, 400, 400);
        p.setLayout(new GridLayout(x, y));
        f.add(p);
        
        f.setMenuBar(menubar);// tao menubar cho frame
        Menu game = new Menu("Game");
        menubar.add(game);
        Menu help = new Menu("Help");
        menubar.add(help);
        MenuItem helpItem = new MenuItem("Help");
        help.add(helpItem);
        MenuItem about = new MenuItem("About ..");
        help.add(about);
        help.addSeparator();
        MenuItem newItem = new MenuItem("New Game");
        game.add(newItem);
        MenuItem exit = new MenuItem("Exit");
        game.add(exit);
        game.addSeparator();
        newItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                        newgame();
                        try {
                                oos.writeObject("newgame,123");
                        } catch (IOException ie) {
                                //
                        }
                }

        });
        exit.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                }
        });
        about.addActionListener(new ActionListener()
        {
            @Override
                public void actionPerformed(ActionEvent e) {
                    //Object[] options = {"OK"};
                 JOptionPane.showConfirmDialog(f,
                                "Lê Trọng Trí_13119044\nTrịnh Duy Thành_13119037", "Information",
                                JOptionPane.CLOSED_OPTION); 
                }
        });
        help.addActionListener(new ActionListener()
        {
            @Override
                public void actionPerformed(ActionEvent e) {
                    //Object[] options = {"OK"};
                 JOptionPane.showConfirmDialog(f,
                                "Luật chơi rất đơn giản bạn chỉ cần 5 ô liên tiếp nhau\n"
                                        + "Theo hàng ngang hoặc dọc hoặc chéo là bạn đã thắng", "Luật Chơi",
                                JOptionPane.CLOSED_OPTION); 
                }
        });
        demthoigian = new JLabel("Thời Gian:");
        demthoigian.setFont(new Font("TimesRoman", Font.ITALIC, 16));
        demthoigian.setForeground(Color.BLACK);
        f.add(demthoigian);
        demthoigian.setBounds(430, 120, 300, 50);
        second = 0;
        minute = 0;
        thoigian = new Timer(1000, new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                        String temp = minute.toString();
                        String temp1 = second.toString();
                        if (temp.length() == 1) {
                                temp = "0" + temp;
                        }
                        if (temp1.length() == 1) {
                                temp1 = "0" + temp1;
                        }
                        /*if (second == 59) {
                                demthoigian.setText("Thời Gian:" + temp + ":" + temp1);
                                minute++;
                                second = 0;
                        } else {
                                demthoigian.setText("Thời Gian:" + temp + ":" + temp1);
                                second++;
                        }*/
                        
                        if (second == 10) {
                                try {
                                        oos.writeObject("checkwin,123");
                                } catch (IOException ex) 
                                              {
                                }
                                Object[] options = { "Dong y", "Huy bo" };
                                int m = JOptionPane.showConfirmDialog(f,
                                                "Ban da thua.Ban co muon choi lai khong?", "Thong bao",
                                                JOptionPane.YES_NO_OPTION);
                                if (m == JOptionPane.YES_OPTION) {
                                        second = 0;
                                        minute = 0;
                                        setVisiblePanel(p);
                                        newgame();
                                        try {
                                                oos.writeObject("newgame,123");
                                        } catch (IOException ie) {
                                                //
                                        }
                                } else if (m == JOptionPane.NO_OPTION) {
                                        thoigian.stop();
                                }
                        } else {
                                demthoigian.setText("Thời Gian:" + temp + ":" + temp1);
                                second++;
                        }

                }

        });
        //khung chat
        Font fo = new Font("Arial",Font.BOLD,15);
        content = new JTextArea();
        content.setFont(fo);
        content.setBackground(Color.white);
        
        content.setEditable(false);
        JScrollPane sp = new JScrollPane(content);
        sp.setBounds(430,170,300,180);
        send = new JButton("Gui");
        send.setBounds(640, 390, 70, 40);
        enterchat = new JTextField("");
        enterchat.setFont(fo);
        enterchat.setBounds(430, 400, 200, 30);
        enterchat.setBackground(Color.white);
        f.add(enterchat);
        f.add(send);
        f.add(sp);
        f.setVisible(true);
        send.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(send))
        {
            try
            {
                
                temp+="Tôi: " + enterchat.getText() + "\n";
                content.setText(temp);
                oos.writeObject("chat," + enterchat.getText());
                enterchat.setText("");
                //temp = "";
                enterchat.requestFocus();
                content.setVisible(false);
                content.setVisible(true);
                
            }
            catch (Exception r)
            {
                r.printStackTrace();
            }
        }
            }
        });
        
        //button caro
        bt = new JButton[x][y];
        for(int i = 0; i < x; i++)
        {
            for(int j = 0; j < y; j++)
            {
                final int a = i, b =j;
                bt[a][b] = new JButton();
                bt[a][b].setBackground(Color.LIGHT_GRAY);
                bt[a][b].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                        flat = true;// server da click
                        thoigian.start();
                        second = 0;
                        minute = 0;
                        matrandanh[a][b] = 1;
                        bt[a][b].setEnabled(false);                        
                        //bt[a][b].setIcon(new ImageIcon(getClass().getResource("o.png")));
                        bt[a][b].setBackground(Color.RED);
                        try{
                            oos.writeObject("caro," + a + "," + b);
                            setEnableButton(false);
                        }
                        catch(Exception ie)
                        {
                            ie.printStackTrace();
                        }
                        thoigian.stop();
                  }

                });                
                p.add(bt[a][b]);
                p.setVisible(false);
                p.setVisible(true);
            }
        }
        
        try {
                    serversocket = new ServerSocket(1234);
                    System.out.println("Dang doi client...");
                    socket = serversocket.accept();
                    System.out.println("Client da ket noi!");
                    os = socket.getOutputStream();
                    is = socket.getInputStream();
                    oos = new ObjectOutputStream(os);
                    ois = new ObjectInputStream(is);
                    while (true) {
                            String stream = ois.readObject().toString();
                            String[] data = stream.split(",");
                            if (data[0].equals("chat")) {
                                    temp += "Khách:" + data[1] + '\n';
                                    content.setText(temp);
                            } else if (data[0].equals("caro")) {
                                    thoigian.start();
                                    second = 0;
                                    minute = 0;
                                    caro(data[1], data[2]);
                                    setEnableButton(true);
                                    
                                    if (winner == false)
                                            setEnableButton(true);
                            }
                            else if (data[0].equals("newgame")) {
                                    newgame();
                                    second = 0;
                                    minute = 0;
                            } 
                            else if (data[0].equals("checkwin")) {
                                    thoigian.stop();
                                    
                            }
                    }
            } catch (Exception ie) {
                    // ie.printStackTrace();
            } //finally {
              //      socket.close();
              //      serversocket.close();
            //}
        
    }
    
    public void newgame() {
            for (int i = 0; i < x; i++)
            {
                    for (int j = 0; j < y; j++) {
                            bt[i][j].setBackground(Color.LIGHT_GRAY);
                            matran[i][j] = 0;
                            matrandanh[i][j] = 0;
                    }
            }
            setEnableButton(true);
            second = 0;
            minute = 0;
            thoigian.stop();
}
    
    public void setVisiblePanel(JPanel pHienthi) {
            f.add(pHienthi);
            pHienthi.setVisible(true);
            pHienthi.updateUI();// ......
	
       }
    
    public void setEnableButton(boolean b) {
            for (int i = 0; i < x; i++)
            {
                    for (int j = 0; j < y; j++) {
                            if (matrandanh[i][j] == 0)
                                    bt[i][j].setEnabled(b);
                    }
            }
    }
    
    //thuat toan tinh thang thua
    public int checkHang() {
            int win = 0, hang = 0, n = 0, k = 0;
            boolean check = false;
            for (int i = 0; i < x; i++) {
                    for (int j = 0; j < y; j++) {
                            if (check) {
                                    if (matran[i][j] == 1) {
                                            hang++;
                                            if (hang > 4) {
                                                    win = 1;
                                                    break;
                                            }
                                            continue;
                                    } else {
                                            check = false;
                                            hang = 0;
                                    }
                            }
                            if (matran[i][j] == 1) {
                                    check = true;
                                    hang++;
                            } else {
                                    check = false;
                            }
                    }
                    hang = 0;
            }
            return win;
    }

    public int checkCot() {
            int win = 0, cot = 0;
            boolean check = false;
            for (int j = 0; j < y; j++) {
                    for (int i = 0; i < x; i++) {
                            if (check) {
                                    if (matran[i][j] == 1) {
                                            cot++;
                                            if (cot > 4) {
                                                    win = 1;
                                                    break;
                                            }
                                            continue;
                                    } else {
                                            check = false;
                                            cot = 0;
                                    }
                            }
                            if (matran[i][j] == 1) {
                                    check = true;
                                    cot++;
                            } else {
                                    check = false;
                            }
                    }
                    cot = 0;
            }
            return win;
    }

    public int checkCheoPhai() {
            int win = 0, cheop = 0, n = 0, k = 0;
            boolean check = false;
            for (int i = x - 1; i >= 0; i--) {
                    for (int j = 0; j < y; j++) {
                            if (check) {
                                    if (matran[n - j][j] == 1) {
                                            cheop++;
                                            if (cheop > 4) {
                                                    win = 1;
                                                    break;
                                            }
                                            continue;
                                    } else {
                                            check = false;
                                            cheop = 0;
                                    }
                            }
                            if (matran[i][j] == 1) {
                                    n = i + j;
                                    check = true;
                                    cheop++;
                            } else {
                                    check = false;
                            }
                    }
                    cheop = 0;
                    check = false;
            }
            return win;
    }

    public int checkCheoTrai() {
            int win = 0, cheot = 0, n = 0;
            boolean check = false;
            for (int i = 0; i < x; i++) {
                    for (int j = y - 1; j >= 0; j--) {
                            if (check) {
                                    if (matran[n - j - 2 * cheot][j] == 1) {
                                            cheot++;
                                            System.out.print("+" + j);
                                            if (cheot > 4) {
                                                    win = 1;
                                                    break;
                                            }
                                            continue;
                                    } else {
                                            check = false;
                                            cheot = 0;
                                    }
                            }
                            if (matran[i][j] == 1) {
                                    n = i + j;
                                    check = true;
                                    cheot++;
                            } else {
                                    check = false;
                            }
                    }
                    n = 0;
                    cheot = 0;
                    check = false;
            }
            return win;
    }
    
    public void caro(String x, String y)
    {
        xx = Integer.parseInt(x);
        yy = Integer.parseInt(y);
        // danh dau vi tri danh
        matran[xx][yy] = 1;
        matrandanh[xx][yy] = 1;
        bt[xx][yy].setEnabled(false);
        //bt[xx][yy].setIcon(new ImageIcon("x.png"));
        bt[xx][yy].setBackground(Color.BLACK);
        
        // Kiem tra thang hay chua
        System.out.println("CheckH:" + checkHang());
        System.out.println("CheckC:" + checkCot());
        System.out.println("CheckCp:" + checkCheoPhai());
        System.out.println("CheckCt:" + checkCheoTrai());
        winner = (checkHang() == 1 || checkCot() == 1 || checkCheoPhai() == 1 || checkCheoTrai() == 1);
        if (checkHang() == 1 || checkCot() == 1 || checkCheoPhai() == 1
                        || checkCheoTrai() == 1) {
                setEnableButton(false);
                thoigian.stop();
                try {
                        oos.writeObject("checkwin,123");
                } catch (IOException ex) 
                                              {
                }
                Object[] options = { "Dong y", "Huy bo" };
                int m = JOptionPane.showConfirmDialog(f,
                                "Ban da thua.Ban co muon choi lai khong?", "Thong bao",
                                JOptionPane.YES_NO_OPTION);
                if (m == JOptionPane.YES_OPTION) {
                        second = 0;
                        minute = 0;
                        setVisiblePanel(p);
                        newgame();
                        try {
                                oos.writeObject("newgame,123");
                        } catch (IOException ie) {
                                //
                        }
                } else if (m == JOptionPane.NO_OPTION) {
                        thoigian.stop();
                }
        }
        
    }
    
    public static void main(String[] args) {
        new Final();
    }
    
}
