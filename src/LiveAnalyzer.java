import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.*;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class LiveAnalyzer extends JFrame implements Runnable
{
    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        SwingUtilities.invokeLater(new LiveAnalyzer());
    }
    static String decompress(String base64) throws Exception {
        byte[] compressed = Base64.getDecoder().decode(base64);
        GZIPInputStream gzip =
                new GZIPInputStream(new ByteArrayInputStream(compressed));
        return new String(gzip.readAllBytes());
    }
    static JTextPane stuff = new JTextPane();
    static StyledDocument doc = stuff.getStyledDocument();
    static Style s = doc.addStyle("style", null);
    static Style t = doc.addStyle("title", null);

    public LiveAnalyzer() throws URISyntaxException, IOException, InterruptedException {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setUndecorated(true);
        setSize(270, 550);
        setLocation(0, 250);
        stuff.setEditable(false);
        stuff.setBackground(new Color(33, 33, 33));
        stuff.setCaretColor(new Color(33, 33, 33));
        StyleConstants.setForeground(s, new Color(180, 50, 180));
        StyleConstants.setForeground(t, new Color(190, 20, 40));
        StyleConstants.setFontSize(s, 16);
        StyleConstants.setFontSize(t, 25);
        StyleConstants.setFontFamily(s, "Monospaced");
        StyleConstants.setBold(t, true);
        add(stuff);
        setVisible(true);
        setText("No game logged yet.");

        String[] typicalPaths = new String[] {
                "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe",
                "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe",
        };
        String chosen = "";
        for (String p : typicalPaths) {
            if (Files.isRegularFile(Path.of(p)))
                chosen = p;
        }
        if (chosen.isEmpty()) {
            // TODO
        }

        ProcessBuilder pb = new ProcessBuilder(chosen, "--remote-debugging-port=9202",
                "--user-data-dir=" + System.getenv("TEMP") + "\\Live2048Analyzer");
        pb.start();

        String webSocketDebuggerURL;
        HttpClient client = HttpClient.newHttpClient();

        while (true) { // waiting for 2048verse to open
            Thread.sleep(1000);

//            registerGame("3x3-1_¾ûw¾úw¾û5W2yÂÃBÈÂyHÂxÀÃyF0xÀ1CEÂ1Y0yT1xPÂ2ÂÊjý1iÎ8jnÊyE8xû1yØ0hÑ0xÐÂhêÂxf8i0gwE8B¤8yû9yÈËxH8yZ8hÑ0ykÌggÊxi8ye8h9ÊmL9iVa3s8yBË348xSÊyaÊ3öÂ1t8xØ8xpÊ1IÊ3CÊ7õ0xBÊ7j81p93oÓ3x85A8jÈÌ0wÊ3Ä0hû03ÈÂ3ÇÊxF93IÊjfÊhIa1ÆËym8xwËhG03OÊ3w8338li93cÒ3É91OÊyCÊ3j8hMÊ7egjbh1Õ8yj8hÓ8i6g1ÛËy2ÒBm8jÌ8hÚ9jÄ8yt8xÎÊyÞ81C81DÊ3ÝÊ3DÊ3ÀÊxrË7ÒÊ3ya06Ê3mË0XÊ7ÔÊ3Ð83EË3Ca0B90L974Ò3eÌ4ã93g81c93äÊ11Óh1gzð9jDÊ0ÜËi7iwÂÊBãËlÎ8By9yÕÊxRÊyFÊyeÊxò87dÊhÊÊxBÌwpÊysÌwR8yJ8xÂ9yHÌwsËy6ÒxU8ynËxshyô8iÄÌA5ÜwA9CÞÊhu8hxÊyù8yq8BØËBriAzÊyØÊxbghÑËxÓÌwhÊybÊhqËyäÊ3aÌwgawhÌwrÊyk8hvaka9yÁÊ3j8xF8xx8hx8iÑawx9xåÌwtÊyy9xL8yA83iÊhp83G83k8xDÊ3BÊ7eÊxÀÊyq8yEÊxE83Gawç0CìÂhu9CÞÊhoÊyDËyEÊxw8xpÊxB8xv8xBÊyE83kÊxz8yA8ywawp8yvËwP8ytaAÕÊyÊÊxd8xq8xA8xV8yvÊ3jaw9aA0Òy98hLÊiáawNÌwcaCÕÊhÏÊhÅ81IÊjzÊ1n9yô8xFËyzÊy7ÒymÊhw93CÊ1Ï8iA9xÀ8jqÊ1qÊjEÊ1F83H87Ø8xÈÊ1Taw÷83ÁÊ1n85jËyF81KÊ31ÒiLËxPÊ3sÊy48xL8yX9CÅËyxawaËyoËwL8yy9xs9wEÊyQayy9wuÌwxÊxA8yyawxÊyw9A0iiWÊ5ÁÌwÄÌwBËyK8xAËxC9ynÊxu8yxËyJ837ÊhG8iQ83MaApËyn8hËËxÌ8jIÊCv8lÊ8yA8hÅÌwr83Ð03c8xo8yBËyDÊhPawm8xEËyB8Bø9xó8y7g3sÊxB83E83kÊxBÊxxawu9ywawCÌwx8yvawCÊyyÊxàÂxÐÂxFÊxJ8yv83bÌAfawHÊyiÊhwaggÊyPËyuÊhZÌwD9xÆ83kÊlQ83FÊxE83y839ÊxT8hnÌyòÊjÂ81zÊxcgxõÊBogyH8xnÊBÓawiËyé9wmÊxk83f8hy8yAÊyy8hz8yOËxU8yN83tÊBØ8hwÊjK81LÊiH9xtawoÊCXÌAzÊyâ8xA8xWÚx");
//            System.exit(0);


            HttpRequest req = HttpRequest.newBuilder(new URI("http://127.0.0.1:9202/json")).build();
            String res = client.send(req, HttpResponse.BodyHandlers.ofString()).body();

            if (res.contains("2048verse")) {
                res = res.substring(res.indexOf("2048verse"));
                Pattern p = Pattern.compile(".*webSocketDebuggerUrl\": \"(.*)\".*", Pattern.MULTILINE);
                Matcher m = p.matcher(res);
                m.find();
                webSocketDebuggerURL = m.group(1);
                break;
            }
        }

        client.newWebSocketBuilder().buildAsync(new URI(webSocketDebuggerURL), new WebSocket.Listener() {
            @Override
            public void onOpen(WebSocket webSocket) {
                webSocket.request(1);
                webSocket.sendText("{\"id\":1,\"method\":\"Network.enable\"}", true);
            }
            String prog;
            @Override
            public CompletionStage<?> onText(WebSocket s, CharSequence str, boolean last) {
                prog += str.toString();
                if (last) {
                    if (prog.contains("backend.2048verse.com/games/upload") && prog.contains("WillBeSent")) {
                        System.out.println(prog);
                        Pattern p = Pattern.compile(".*\"bytes\":\"(.*)\"}]");
                        Matcher m = p.matcher(prog);
                        m.find();
                        String decoded = null;
                        boolean skipping = false;
                        try {
                            decoded = decompress(m.group(1));
                        } catch (Exception e) { e.printStackTrace(); skipping = true; }
                        if (!skipping) {
                            Pattern p2 = Pattern.compile(".*pgn\":\"(.*)\",\"main_times.*");
                            Matcher m2 = p2.matcher(decoded);
                            m2.find();
                            String replay = m2.group(1);
                            System.out.println(replay);
                            registerGame(replay);
                        }
                    }
                    prog = "";
                }
                s.request(1);
                return null;
            }
        }).join();



    }

    public void setText(String text) {
        stuff.setText("");
        try {
            doc.insertString(0, text, s);
            doc.insertString(0, "Last Game's Analysis:\n", t);
        } catch (BadLocationException _) {}
    }

    @Override
    public void run() {

    }

    static String alph = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿ¤¾";
    static HashMap<Character, Integer> iLookup = new HashMap<>();
    static {
        for (int x = 0; x < alph.length(); x++)
            iLookup.put(alph.charAt(x), x);
    }
    int[] timebase = new int[] {0, 256, 1024, 4096, 16384, 65536, 262144, 1048576}; // max = 4182016
    int[] timeres = new int[] {1, 3, 12, 48, 192, 768, 3072, 12288};

    public void registerGame(String rpl) {
        rpl = rpl.substring(rpl.indexOf("_") + 1);
        System.out.println(rpl);
        ArrayList<Integer> moves = new ArrayList<>();
        ArrayList<Integer> vals = new ArrayList<>();
        ArrayList<Integer> x = new ArrayList<>();
        ArrayList<Integer> y = new ArrayList<>();
        ArrayList<Integer> times = new ArrayList<>();
        for (int i = 0; i < rpl.length(); i += 3) {
            String substr = rpl.substring(i, i + 3);
            int val = (iLookup.get(substr.charAt(0)) << 14)
                    | (iLookup.get(substr.charAt(1)) << 7) | iLookup.get(substr.charAt(2));
            moves.add(val & 0b11);
            vals.add((val >> 2) & 0b11);
            x.add((val >> 4) & 0b111);
            y.add((val >> 7) & 0b111);
            int res = (val >> 10) & 0b111;
            times.add(timebase[res] + timeres[res] * ((val >> 13) & 0xFF));
        }
//        for (int i = 0; i < moves.size(); i++) {
//            System.out.printf("%d %d %d %d %d\n", moves.get(i), vals.get(i), x.get(i), y.get(i), times.get(i));
//        }
        ArrayList<Double> moveAccs = ama(moves, vals, x, y);
        System.out.println(moveAccs);
        double perf = 0;
        double geom = 0;
        double gof = 1;
        int strk = 0;
        int bstrk = 0;
        for (double d : moveAccs) {
            if (d >= 1 - 0.00000000000001) {
                perf++;
                strk++;
            }
            else {
                if (strk > bstrk)
                    bstrk = strk;
                strk = 0;
            }
            gof *= d;
            geom += Math.log(d);
        }
        perf /= moveAccs.size();
        geom = Math.exp(geom / moveAccs.size());
        double[] scores = score(times, vals, bstrk, perf, geom);
        String grade = "X";
        if (scores[4] < 1) grade = "F";
        else if (scores[4] < 3) grade = "E";
        else if (scores[4] < 5) grade = "D";
        else if (scores[4] < 7) grade = "C";
        else if (scores[4] < 9) grade = "B";
        else if (scores[4] < 12) grade = "A";
        else if (scores[4] < 15) grade = "S";
        else if (scores[4] < 18) grade = "SS";
        else if (scores[4] < 21) grade = "SSS";


        String text = String.format("Streak : %d\nGeom   : %2.5f%%\nPerf   : %2.2f%%\nGof    : %2.2f%%\nScore  : %2.2f (%s)", bstrk, 100 * geom, 100 * perf, 100 * gof, scores[4], grade);
        System.out.println(Arrays.toString(scores));
        setText(text);
        System.out.println(text);
    }

    public double[] score(ArrayList<Integer> times, ArrayList<Integer> vals, double... arr) {
        double[] bins1 = new double[] {13, 17, 27, 90, 158, 186, 203, 225, 290};
        double[] bins2 = new double[] {0.63, 0.67, 0.73, 0.85, 0.88, 0.895, 0.91, 0.92, 0.93};
        double[] bins3 = new double[] {0.994, 0.9979, 0.9992, 0.99993, 0.999984, 0.9999925, 0.9999957, 0.9999976, 0.999999};
        double[][] stuff = new double[][] {bins1, bins2, bins3};
        double[] ret = new double[5];
        for (int i = 0; i < 3; i++) {
            int base = -1;
            for (int j = 0; j < 9; j++) {
                if (arr[i] < stuff[i][j]) {
                    base = j;
                    break;
                }
            }
            if (base == 0)
                ret[i] = 0;
            else if (base == -1)
                ret[i] = 9;
            else {
                double score0 = stuff[i][base - 1];
                double score1 = stuff[i][base];
                double delta = score1 - score0;
                ret[i] = base + (arr[i] - score0) / delta;
            }
        }
        int sum = 0;
        int timeMs = -4000;
        for (int i : vals)
            sum += 2 << i;
        for (int i : times)
            if (i == 4182016)
                timeMs += 2000;
            else
                timeMs += i;
        double[] timeStuff;
        if (sum >= 1024)
            timeStuff = new double[] {660000, 480000, 300000};
        else
            timeStuff = new double[] {270000, 195000, 120000};
        if (sum >= 1536)
            ret[3] = 3;
        else {
            int base = -1;
            for (int i = 0; i < 3; i++)
                if (timeStuff[i] <= timeMs) {
                    base = i;
                    break;
                }
            if (base == 0)
                ret[3] = 0;
            else if (base == -1)
                ret[3] = 3;
            else {
                double score0 = timeStuff[base - 1];
                double score1 = timeStuff[base];
                double delta = score0 - score1;
                ret[3] = base + ((double) timeMs - score1) / delta;
            }
            System.out.println(timeMs);
        }
        ret[4] = ret[0] + ret[1] + ret[2] + ret[3];
        return ret;
    }

    public ArrayList<Double> ama(ArrayList<Integer> moves, ArrayList<Integer> vals, ArrayList<Integer> xPos,
                                 ArrayList<Integer> yPos) {
        ArrayList<Double> ret = new ArrayList<>();
        Board3x3 board = new Board3x3(0,0,0,0,0,0,0,0,0);
        board.spawn(xPos.get(0), yPos.get(0), vals.get(0));
        board.spawn(xPos.get(1), yPos.get(1), vals.get(1));
        for (int x = 2; x < moves.size(); x++) {
            Move optimal = Board3x3.getMove2(board);
            int move = moves.get(x);
            ret.add(optimal.getScore(move) / optimal.getBestScore());
            if (ret.getLast() == 0)
                System.out.println(board + " " + moves.get(x));
            board.name = board.merge(switch(move) {
                case 0 -> 2;
                case 1 -> 0;
                case 2 -> 1;
                case 3 -> 3;
                default -> -1;
            });
            if (ret.getLast() == 0)
                System.out.println("to " + board);
            board.spawn(xPos.get(x), yPos.get(x), vals.get(x));
        }


        return ret;
    }

}