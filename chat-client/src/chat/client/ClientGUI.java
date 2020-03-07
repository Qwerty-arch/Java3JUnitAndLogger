package chat.client;

import chat.common.Library;
import network.SocketThread;
import network.SocketThreadListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;

public class ClientGUI extends JFrame implements ActionListener, Thread.UncaughtExceptionHandler, SocketThreadListener {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 300;

    private final JTextArea log = new JTextArea();
    private final JPanel panelTop = new JPanel(new GridLayout(2, 3));
    private final JTextField tfIPAddress = new JTextField("127.0.0.1");
    private final JTextField tfPort = new JTextField("8189");
    private final JCheckBox cbAlwaysOnTop = new JCheckBox("Always on top");
    private final JTextField tfLogin = new JTextField("Ivan");
    private final JPasswordField tfPassword = new JPasswordField("123");
    private final JButton btnLogin = new JButton("Login");

    private final JPanel panelBottom = new JPanel(new BorderLayout());
    private final JButton btnDisconnect = new JButton("<html><b>Disconnect</b></html>");       // передаем размер шрифта(жирный шрифт)
    private final JTextField tfMessage = new JTextField();
    private final JButton btnSend = new JButton("Send");

    private final JList<String> userList = new JList<>();                                           // чтобы хранить список пользователей
    private boolean shownIoErrors = false;
    private SocketThread socketThread;
    private final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss: ");
    private final String WINDOW_TITLE = "Chat";                                                     // т.к. мы наш Title будем использовать много где, нам нужно вынести его

    private ClientGUI() {
        Thread.setDefaultUncaughtExceptionHandler(this);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);                                                                 // чтобы хранить список пользователей
        setTitle(WINDOW_TITLE);
        setSize(WIDTH, HEIGHT);
        log.setEditable(false);
        log.setLineWrap(true);
        JScrollPane scrollLog = new JScrollPane(log);
        JScrollPane scrollUser = new JScrollPane(userList);
        scrollUser.setPreferredSize(new Dimension(100, 0));                            // задаем размеры нашего окошка с юзерами
        cbAlwaysOnTop.addActionListener(this);                                                    // создали лисенер для Always on top
        btnSend.addActionListener(this);
        tfMessage.addActionListener(this);
        btnLogin.addActionListener(this);
        btnDisconnect.addActionListener(this);
        panelBottom.setVisible(false);                                                               // изначально наша панелька невидима

        panelTop.add(tfIPAddress);
        panelTop.add(tfPort);
        panelTop.add(cbAlwaysOnTop);
        panelTop.add(tfLogin);
        panelTop.add(tfPassword);
        panelTop.add(btnLogin);
        panelBottom.add(btnDisconnect, BorderLayout.WEST);
        panelBottom.add(tfMessage, BorderLayout.CENTER);
        panelBottom.add(btnSend, BorderLayout.EAST);

        add(scrollLog, BorderLayout.CENTER);
        add(scrollUser, BorderLayout.EAST);
        add(panelTop, BorderLayout.NORTH);
        add(panelBottom, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void connect() {
        try {
            Socket socket = new Socket(tfIPAddress.getText(), Integer.parseInt(tfPort.getText())); // создаем новый Soket (сетевая составляющая клиента); tfIPAddress.getText() - забрали IP адрес из нашего поля с IP адресом, Integer.parseInt(tfPort.getText()) - получаем порт
            socketThread = new SocketThread(this, "Client", socket);                // как только соединились нам нужно создать новый SocketThread, т.е. сам сокет есть, теперь Thread // listener - это мы(this), name - клиент, socket - тот, что только что сгенерили
        } catch (IOException e) {
            showException(Thread.currentThread(), e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() { // Event Dispatching Thread
                new ClientGUI();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == cbAlwaysOnTop) {                     // "когда источник события "Always on top""
            setAlwaysOnTop(cbAlwaysOnTop.isSelected()); // назначаем смену положения нашему CheakBox(проверяем, если галочка стоит => true, если нет, то наоборот)
        } else if (src == btnSend || src == tfMessage) {
            sendMessage();
        } else if (src == btnLogin) {
            connect();
        } else if (src == btnDisconnect) {              // внутри button дисконект нужно закрыть сокет и панелька логина появляется т.к. дисконект может быть неудачным и дисконект может произойти не понашей вине
            socketThread.close();
        } else {
            throw new RuntimeException("Unknown source: " + src);
        }
    }

    private void sendMessage() {
        String msg = tfMessage.getText();
        String username = tfLogin.getText();
        if ("".equals(msg)) return;
        tfMessage.setText(null);
        tfMessage.requestFocusInWindow();
        socketThread.sendMessage(Library.getTypeBcastClient(msg));      // мы должны не просто посылать сообщение(msg), а Library.getTypeBcastClient(msg)
        //wrtMsgToLogFile(msg, username);
    }

    private void wrtMsgToLogFile(String msg, String username) {
        try (FileWriter out = new FileWriter("log.txt", true)) {
            out.write(username + ": " + msg + "\n");
            out.flush();
        } catch (IOException e) {
            if (!shownIoErrors) {
                shownIoErrors = true;
                showException(Thread.currentThread(), e);
            }
        }
    }

    private void putLog(String msg) {
        if ("".equals(msg)) return;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }

    private void showException(Thread t, Throwable e) {
        String msg;
        StackTraceElement[] ste = e.getStackTrace();
        if (ste.length == 0)
            msg = "Empty Stacktrace";
        else {
            msg = "Exception in " + t.getName() + " " +
                    e.getClass().getCanonicalName() + ": " +
                    e.getMessage() + "\n\t at " + ste[0];
        }
        JOptionPane.showMessageDialog(null, msg, "Exception", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        showException(t, e);
        System.exit(1);
    }

    /**
     * Socket thread listener methods
     * */

    @Override
    public void onSocketStart(SocketThread thread, Socket socket) {
        putLog("Start");
    }

    @Override
    public void onSocketStop(SocketThread thread) {     // сделали невидемой нижнюю панельку и видимой верхнюю
        panelBottom.setVisible(false);
        panelTop.setVisible(true);
        setTitle(WINDOW_TITLE);                         // Title меняем назад на обычный
        userList.setListData(new String[0]);            // после отключения нам надо список пользователей очистить // никаких старых пользователей там не видеть // => пустой массив
    }

    @Override
    public void onSocketReady(SocketThread thread, Socket socket) {     // когда сокет готов
        panelBottom.setVisible(true);  // сделали видимой
        panelTop.setVisible(false);    // сделали невидимой
        String login = tfLogin.getText();                           // логин
        String password = new String(tfPassword.getPassword());     // пароль
        thread.sendMessage(Library.getAuthRequest(login, password));// из library достаем AuthRequest и посылаем сообщение с AuthRequest
    }

    @Override
    public void onReceiveString(SocketThread thread, Socket socket, String msg) {
        handleMessage(msg);
    }       // обработка получения строки

    @Override
    public void onSocketException(SocketThread thread, Exception exception) {
        // showException(thread, exception);     // убрали, что-бы не сбивали с толку
    }

    private void handleMessage(String msg) {                                    // метод отфармотирования строки (преводим сообщение в нормальный вид)
        String[] arr = msg.split(Library.DELIMITER);
        String msgType = arr[0];                                                // мы знаем, что тип сообщения у нас всегда лежит в 0-ом элементе массива(благодоря Library)
        switch (msgType) {
            case Library.AUTH_ACCEPT:
                setTitle(WINDOW_TITLE + " entered with nickname: " + arr[1]);   // говорим, что мы вошли в чат с nickname       // Title мы будем менять назад в методе onSocketStop(либо когда мы сами вышли, либо когда сервак упал)
                break;
            case Library.AUTH_DENIED:
                putLog(msg);                                                    // нечего, просто дисконектимся и пишем в putlog
                break;
            case Library.MSG_FORMAT_ERROR:
                putLog(msg);
                socketThread.close();                                           // закрыли сокет
                break;
            case Library.TYPE_BROADCAST:
                putLog(DATE_FORMAT.format(Long.parseLong(arr[1])) +       // отшармотировали по Date Format
                        arr[2] + ": " + arr[3]);
                break;
            case Library.USER_LIST:                                             //
                String users = msg.substring(Library.USER_LIST.length() +       // Сплитать по Delimetr смысла нет // создаем String users - взять и из нашего сообщения выделить только пользователей (substring) // Как это сделать ? // взять и отрезать ту часть сообщения, где у нас Delimetr и тип сообщения // т.е. начало будет не в 0, а после префикса и делиметра // взяли длину префикса + длину Delimetr и отрезали (substring) // теперь у нас в строке юзеров только юзеры, разделенные Delimetr
                        Library.DELIMITER.length());
                String[] usersArr = users.split(Library.DELIMITER);             // по DELIMITER поделили
                Arrays.sort(usersArr);                                          // отсартировали
                userList.setListData(usersArr);
                break;
            default:                                                            // на случай, если пришло совсем непонятное сообщение(не подходящие под MSG_FORMAT_ERROR или ещё чего-то) // тут что-то, что не описано в Library
                throw new RuntimeException("Unknown message type: " + msg);
        }
    }
}
