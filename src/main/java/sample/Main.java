package sample;

public class Main {

    public static void main(String[] args) throws Exception {

        //Generate JSON files from Articles
        for (int i = 2010; i >= 2019; i--) {
            Utility.getInstance().setYear(i);
            Utility.getInstance().executeProgram(i);
        }

        //read serialized objects
        //Utility.getInstance().setYear(2013);
        //Utility.getInstance().deserializationObject(198 + "");
    }
}

