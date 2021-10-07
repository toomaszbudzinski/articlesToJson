package sample;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;

public class Utility {

    private static Utility INSTANCE = new Utility();

    //config variables
    private boolean sortDesc;
    private int year;
    private FTPfileList ftPfileList;
    private String linkFTP, workDirectory, urlFTP, headingArticleBegin, headingArticleEnd;
    private final boolean isTestOn = false; //test before save to new object
    private Properties properties;

    private Utility() {
        if (INSTANCE != null) {
            throw new IllegalStateException("Utility.class : Singleton already constructed");
        }

        properties = new Properties();
        try {
            InputStream input = ClassLoader.getSystemClassLoader().getResourceAsStream("ftppass.properties");
            properties.load(input);
        } catch (IOException io) {
            io.printStackTrace();
        }
        year = Calendar.getInstance().get(Calendar.YEAR);
        setUrlFTP(getProperty("url"));
        setHeadingArticleBegin(getProperty("headingArticleBegin"));
        setHeadingArticleEnd(getProperty("headingArticleEnd"));
        sortDesc = true;
    }

    public static Utility getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Utility();
        }
        return INSTANCE;
    }

    public String getProperty(String name) {
        return properties.getProperty(name);
    }

    public boolean isSortDesc() {
        return sortDesc;
    }

    public void setSortDesc(boolean sortDesc) {
        this.sortDesc = sortDesc;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getLinkFTP() {
        return linkFTP;
    }

    public void setLinkFTP(String linkFTP) {
        this.linkFTP = linkFTP;
    }

    public String getWorkDirectory() {
        return workDirectory;
    }

    public void setWorkDirectory(String workDirectory) {
        this.workDirectory = workDirectory;
    }

    public boolean isTestOn() {
        return isTestOn;
    }

    public void setUrlFTP(String urlFTP) {
        this.urlFTP = urlFTP;
    }

    public String getUrlFTP() {
        return urlFTP;
    }

    public String getHeadingArticleBegin() {
        return headingArticleBegin;
    }

    public void setHeadingArticleBegin(String headingArticleBegin) {
        this.headingArticleBegin = headingArticleBegin;
    }

    public String getHeadingArticleEnd() {
        return headingArticleEnd;
    }

    public void setHeadingArticleEnd(String headingArticleEnd) {
        this.headingArticleEnd = headingArticleEnd;
    }

    public void serializationOject(String fileName, Article article) throws Exception {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("ObjectAsBin\\" + fileName + ".bin"));
        objectOutputStream.writeObject(article);
        objectOutputStream.close();
    }

    public void deserializationObject(String fileName) throws Exception {
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("ObjectAsBin\\" + fileName + "_" + getYear() + ".bin"));
        Article article = (Article) inputStream.readObject();
        inputStream.close();
        System.out.println(article.getName());
        System.out.println(article.getDescription());
        System.out.println(article.getFile());
    }

    public void writeJSONtoFile(String jsonArray1asString, int year) throws IOException {
        Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("JSON\\" + year + ".json"), "UTF-8"));
        try {
            out.write(jsonArray1asString);
            System.out.println("Saved to file " + year + ".json");
        } finally {
            out.close();
        }
    }

    //Test - All articles saved to the file
    public void testJSON(JSONArray jsonArray, int year) {
        int j = jsonArray.length();
        for (int ii = j; ii > 1; ii--) {
            for (int ik = 1; ik < jsonArray.length() + 1; ik++) {
                if (jsonArray.getJSONObject(ik - 1).toString().contains(" " + (ii - 1) + "/" + year)) {
                    break;
                }
                if (ik == jsonArray.length())
                    System.out.println("Missing: " + (ii - 1));
            }
        }
    }

    //Check - Article's description has title
    public void testArticleWithDesc(String article, String desc) throws Exception {
        boolean correct = false;
        ArrayList<Integer> arrayIntervalTest;
        if (article.contains("-")) {
            arrayIntervalTest = recognizeInterval(article);
            for (Integer arrayIntervalFor : arrayIntervalTest) {
                if (article.contains(arrayIntervalFor.toString())) {
                    correct = true;
                    break;
                }
            }
        } else {
            if ((desc.contains(article))) {
                correct = true;
            }
        }
        if (correct == false)
            throw new Exception("Incompatible name: " + article + " with desc: " + desc);
    }

    // Input: 1 - 4  = Return: 1, 2, 3, 4
    public ArrayList<Integer> recognizeInterval(String input) {
        ArrayList<Integer> intervalList = new ArrayList<Integer>();
        String[] arraySplit = input.split(" ", -1);
        String[] arraySplitYear = arraySplit[2].split("/", -1);
        String[] arraySplitInterval = arraySplitYear[0].split("-");
        for (int i = Integer.valueOf(arraySplitInterval[1]); i >= Integer.valueOf(arraySplitInterval[0]); i--) {
            intervalList.add(i);
        }
        return intervalList;
    }

    //Get number from article name
    public String getNumber(String input) {
        String[] arraySplit = input.split(" ", -1);
        String[] arraySplitSlash = arraySplit[2].split("/");
        return arraySplitSlash[0];
    }

    public String getArticleByYear(int year) {
        switch (year) {
            case 2010: {
                setWorkDirectory(getProperty("workDirectory"));
                setLinkFTP(getProperty("2010LinkFTP"));
                return getProperty(year + "");
            }
            default: {
                setWorkDirectory(getProperty("workDirectory") + year + "/");
                setLinkFTP(getProperty("linkFTP") + "" + getYear() + "/");
                return getProperty(year + "");
            }
        }
    }


    //Find - Longer than usual text
    public void printLongerText(String text, int charsNumber) {
        if (text.length() > Integer.valueOf(charsNumber))
            System.out.println(text + "");
    }

    public String propablyNameFile(String name) {
        switch (getYear()) {
            case 2010:
                return getProperty("2010FileBeginFTP") + name + "_2010";
            case 2011:
                return name;
            default:
                return name + "_" + getYear();
        }
    }

    public String findNameOfFilePdf(ArrayList<String> arrayOfFile, String numberFind) {
        for (String file : arrayOfFile) {
            if ((propablyNameFile(numberFind) + ".pdf").equals(file))
                return propablyNameFile(numberFind) + ".pdf";
            if ((propablyNameFile(numberFind) + ".PDF").equals(file))
                return propablyNameFile(numberFind) + ".PDF";
        }

        return "";
    }

    public void executeProgram(int year) throws Exception {
        ArrayList<String> arrayFileFromFTP;
        ArrayList<Article> arrayArticleComplete;
        ArrayList<String> tmpArticle;
        ArrayList<String> tmpDesc;
        Document document;
        Elements divsdiv;
        Elements divHeading;
        Elements divDescript;
        ArrayList<Integer> arrayInterval;

        setYear(year);
        arrayArticleComplete = new ArrayList<>();
        tmpArticle = new ArrayList<>();
        tmpDesc = new ArrayList<>();

        document = Jsoup.connect(getArticleByYear(getYear())).followRedirects(false).timeout(60000/*wait up to 60 sec for response*/).get();
        divsdiv = document.body().select(getProperty("mainDiv"));

        System.setProperty("java.net.preferIPv4Stack", "true");
        ftPfileList = new FTPfileList(getUrlFTP(), getWorkDirectory(), getProperty("login"), getProperty("password"));
        arrayFileFromFTP = ftPfileList.getAttachments();
        System.out.println("----------" + getYear() + "----------");

        divHeading = divsdiv.select("a");
        divDescript = divsdiv.select("p");


        for (Element el : divHeading) {
            if (el.ownText().equals("") || el.ownText().equals("»"))
                continue;
            tmpArticle.add(el.ownText());
            printLongerText(el.ownText(), Integer.parseInt(getProperty("stringLenght")));

        }
        for (Element el : divDescript) {
            if (el.ownText().equals(""))
                continue;
            tmpDesc.add(el.ownText());
        }

        //arrayFileFromFTP.forEach(System.out::println);

        for (int i = 0; i < tmpArticle.size(); i++) {
            if (tmpArticle.get(i).contains("-") && !(tmpArticle.get(i).contains(" - EZ") || tmpArticle.get(i).contains("126B") || tmpArticle.get(i).contains("623B"))) {
                arrayInterval = recognizeInterval(tmpArticle.get(i));
                for (Integer arrayIntervalFor : arrayInterval) {
                    if ((findNameOfFilePdf(arrayFileFromFTP, arrayIntervalFor.toString())).equals(propablyNameFile(arrayIntervalFor.toString()) + "" + ".pdf") ||
                            (findNameOfFilePdf(arrayFileFromFTP, arrayIntervalFor.toString())).equals(propablyNameFile(arrayIntervalFor.toString()) + "" + ".PDF")) {
//                        if (isTestOn())
//                                    testArticleWithDesc(tmpArticle.get(i), tmpDesc.get(i));
                        arrayArticleComplete.add(new Article(getHeadingArticleBegin() + " " + arrayIntervalFor.toString() + "/" + getYear() + " " + getHeadingArticleEnd(), tmpDesc.get(i), getYear(), "", getLinkFTP() + "" + findNameOfFilePdf(arrayFileFromFTP, arrayIntervalFor.toString())));
                    } else {
//                        if (isTestOn())
//                                    testArticleWithDesc(tmpArticle.get(i), tmpDesc.get(i));
                        arrayArticleComplete.add(new Article(getHeadingArticleBegin() + " " + arrayIntervalFor.toString() + "/" + getYear() + " " + getHeadingArticleEnd(), tmpDesc.get(i), getYear(), "", ""));
                    }
                }
            } else {
                if ((findNameOfFilePdf(arrayFileFromFTP, getNumber(tmpArticle.get(i)))).equals(propablyNameFile(getNumber(tmpArticle.get(i))) + "" + ".pdf") ||
                        (findNameOfFilePdf(arrayFileFromFTP, getNumber(tmpArticle.get(i)))).equals(propablyNameFile(getNumber(tmpArticle.get(i))) + "" + ".PDF")) {
                    arrayArticleComplete.add(new Article(getHeadingArticleBegin() + " " + getNumber(tmpArticle.get(i)) + "/" + getYear() + " " + getHeadingArticleEnd(), tmpDesc.get(i), getYear(), "", getLinkFTP() + "" + findNameOfFilePdf(arrayFileFromFTP, getNumber(tmpArticle.get(i)))));
                } else {
                    arrayArticleComplete.add(new Article(getHeadingArticleBegin() + " " + getNumber(tmpArticle.get(i)) + "/" + getYear() + " " + getHeadingArticleEnd(), tmpDesc.get(i), getYear(), "", ""));
                }
            }
        }

        //Sort by desc = true || asc = false
        //setSortDesc(false);

        //arrayArticleComplete.forEach(System.out::println);

        //Sort Array
//        Collections.sort(arrayArticleComplete);
//        for (Article article : arrayArticleComplete) {
//            System.out.println(article);
//        }

        //Serialization articles
//        for (Article article : arrayArticleComplete) {
//            serializationOject(getNumber(article.getName()) + "_" + getYear(), article);
//        }

        //Computer JSON
        JSONArray jsonArray = new JSONArray(arrayArticleComplete);
//        System.out.print(jsonArray);

        //Human JSON
        JsonParser parser = new JsonParser();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement el = parser.parse(String.valueOf(jsonArray));
        String jsonArray1asString = gson.toJson(el);
//        System.out.println(jsonArray1asString);

        //write to file
        //writeJSONtoFile(jsonArray1asString, getYear());

//      TEST
        testJSON(jsonArray, getYear());
    }
}
