import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        ArrayList<MonUser> list = calculFreqTweet();
        System.out.println("Hello World!" + list.toString());

    }

    private static ArrayList<MonUser> calculFreqTweet(){
        ArrayList<MonUser> list = new ArrayList<MonUser>();
        for(int i=0;i<30;i++){
            if(i % 2 == 0){   //Je conidère que un chiffre est atypique si il est paire
                MonUser user = new MonUser(i,true);
                list.add(user);
            }else{
                MonUser user = new MonUser(i,false);
                list.add(user);
            }


        }
        return list;
    }

    private static class MonUser{
        int chiffre;
        boolean estAtypique;
        public MonUser(int ch, boolean atip){
            this.chiffre = ch;
            this.estAtypique = atip;
        }

        @Override
        public String toString() {
            return "MonUser{" +
                    "chiffre=" + chiffre +
                    ", estAtypique=" + estAtypique +
                    '}';
        }
    }
}


