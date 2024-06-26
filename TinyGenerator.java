import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;

public class TinyGenerator {

    public static void generate(LinkedList<String> IR){
        LinkedList<String> code = new LinkedList<>();
        code.add(";tiny code");
        int rCounter=0;

        for(int i = 0; i < IR.size(); i++){
            String line = IR.get(i);
            // Warning to look for end of IR code
            if(line.equals(";IR Code") || line.equals(";LABEL main") || line.equals(";LINK") ){
                continue;
            }
            // test
//            System.out.println(line);
            String[] parts = line.split(" ");


            for(int j = 0; j < parts.length; j++){
                if (parts[j].startsWith("$T")) {
                    int index = Integer.parseInt(parts[j].substring(2)) - 1;
                    parts[j] = parts[j].replace("$T" + (index + 1), "r" + index);
                }
            }


            if(parts[0].equals(";VARDCL")){
                code.add("var "+ parts[1]);
                //System.out.println("var "+parts[1]);
            }
            else if(parts[0].equals(";STRDCL")){
                code.add("str "+ parts[1] + " "+ parts[2]);
            }

            else if(parts[0].equals(";STOREI") || parts[0].equals(";STOREF")){
                code.add("move "+ parts[1] + " "+ parts[2]);

            }

            else if(parts[0].equals(";READI")){
                code.add("sys readi "+ parts[1]);
            }
            else if(parts[0].equals(";READF")){
                code.add("sys readf "+ parts[1]);
            }

            else if(parts[0].equals(";WRITEI")){
                code.add("sys writei "+ parts[1]);
            }
            else if(parts[0].equals(";WRITEF")){
                code.add("sys writer "+ parts[1]);
            }
            else if(parts[0].equals(";WRITES")){
                code.add("sys writes "+ parts[1]);
            }

            else if(parts[0].equals(";ADDI")){
                code.add("move "+ parts[1]+ " "+ parts[3]);
                code.add("addi "+ parts[2]+ " "+ parts[3]);
            }
            else if(parts[0].equals(";ADDF")){
                code.add("move "+ parts[1]+ " "+ parts[3]);
                code.add("addr "+ parts[2]+ " "+ parts[3]);
            }

            else if(parts[0].equals(";SUBI")){
                code.add("move "+parts[1]+ " "+ parts[3]);
                code.add("subi "+ parts[2]+ " "+ parts[3]);
            }
            else if(parts[0].equals(";SUBF")){
                code.add("move "+ parts[1]+ " "+parts[3]);
                code.add("subr "+ parts[2]+ " "+ parts[3]);
            }

            else if(parts[0].equals(";MULTI")){
                code.add("move "+ parts[1]+ " "+ parts[3]);
                code.add("muli "+ parts[2]+ " "+ parts[3]);

            }
            else if(parts[0].equals(";MULTF")){
                code.add("move "+ parts[1]+ " "+parts[3]);
                code.add("multr "+ parts[2]+ " "+ parts[3]);
            }

            else if(parts[0].equals(";DIVI")){
                code.add("move "+ parts[1]+ " "+ parts[3]);
                code.add("divi "+ parts[2]+ " "+ parts[3]);
            }
            else if(parts[0].equals(";DIVF")){
                code.add("move "+ parts[1]+ " "+ parts[3]);
                code.add("divr "+ parts[2]+ " "+ parts[3]);
            }

        }

        code.add("sys halt");

        for(String lineIR : IR){
            if(lineIR.contains(";VARDCL") || lineIR.contains(";STRDCL")){
                continue;
            }
            System.out.println(lineIR);
        }
        for(String lineTiny : code){

            System.out.println(lineTiny);

        }
    }

}
