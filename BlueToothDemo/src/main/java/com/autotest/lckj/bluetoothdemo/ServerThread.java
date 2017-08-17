package com.autotest.lckj.bluetoothdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ServerThread implements Runnable {
    // 定义当前线程所处理的Socket
    Socket s = null;
    // 该线程所处理的Socket所对应的输入流
    BufferedReader br = null;

    public ServerThread(Socket s) throws IOException {
        this.s = s;
        // 初始化该Socket对应的输入流
        br = new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    public void run() {
        try
        {
            String content = null;
            //采用循环不断从Socket中读取客户端发送过来的数据
            while((content = readFromClient()) != null)
            {
                //遍历socketList中的每个socket
                //将读到的内容向每一个socket发送一次
                for(Socket s : MyService.socketList)
                {
                    OutputStream os = s.getOutputStream();
                    os.write((content + "\n").getBytes("utf-8"));
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    //定义读取客户端数据的方法
    private String readFromClient() {
        try
        {
            return br.readLine();
        }
        //如果捕捉到异常,表明该Socket对应的客户端已经关闭
        catch(IOException e)
        {
            //删除该Socket
            MyService.socketList.remove(s);
        }
        return null;
    }
}  

