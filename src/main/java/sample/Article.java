package sample;

public class Article implements java.io.Serializable, Comparable<Article> {
    private String name;
    private String description;
    private int year;
    private String previous;
    private String file;

    public Article(String name, String description, int year, String previous, String file) {
        this.name = name;
        this.description = description;
        this.year = year;
        this.previous = previous;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "Article{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", year=" + year +
                ", previous='" + previous + '\'' +
                ", file='" + file + '\'' +
                '}';
    }

    //    @Override
//    public int compareTo(Article o) {
//        int compareNumber = Integer.valueOf(Utility.getInstance().getNumber(((Article)o).getName()));
//        return Integer.valueOf(Utility.getInstance().getNumber(this.getName())) - compareNumber;
//    }

    @Override
    public int compareTo(Article o) {
        int compareNumber;
        int currentNumber;
        try {
            compareNumber = Integer.valueOf(Utility.getInstance().getNumber(((Article) o).getName()));
            currentNumber = Integer.valueOf(Utility.getInstance().getNumber(this.getName()));
        } catch (java.lang.NumberFormatException e) {
            String tmp1 = ((Article) o).getName().replaceAll("-[0-9]+[A-Z].", "");
            String tmp2 = this.getName().replaceAll("-[0-9]+[A-Z].", "");
            compareNumber = Integer.valueOf(Utility.getInstance().getNumber((tmp1.replaceAll("[a-zA-Z]+", ""))));
            currentNumber = Integer.valueOf(Utility.getInstance().getNumber(tmp2.replaceAll("[a-zA-Z]+", "")));
        }
        if (Utility.getInstance().isSortDesc()) {
            return compareNumber - currentNumber; //desc
        } else {
            return currentNumber - compareNumber; //asc
        }
    }
}
