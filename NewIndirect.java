import java.io.*;
import java.util.*;

public class NewIndirect {
    ArrayList<ArrayList<String>> trans;
    Queue<String> nSet;
    HashMap<String,Integer> symNum;
    HashMap<Integer,String> numSym;
    ArrayList<String> original;
    HashSet<String> newNterm;
    public NewIndirect(){
        newNterm = new HashSet<>();
        original = new ArrayList<>();
        trans = new ArrayList<>();
        nSet = new LinkedList<>();
        symNum = new HashMap<>();
        numSym = new HashMap<>();
    }

    int getPos(String searchNt, ArrayList<String> a ){
        for(int i=0;i<a.size();i++){
            if(a.get(i).equals(searchNt))
                return i;
        }
        return -1;
    }

    boolean contains(String searchNt, ArrayList<String> temp ){
        for(int i = 0; i<temp.size(); i++){
            if(temp.get(i).equals(searchNt))
                return true;
        }
        return false;
    }


    public static void main(String[] args) throws Exception {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the number of Transitions : ");
        int n = s.nextInt();
        FileInputStream fIs = new FileInputStream("input.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(fIs));
        FileOutputStream fOs = new FileOutputStream("outputComp.txt");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fOs));
        NewIndirect store = new NewIndirect();


        String str;
        StringTokenizer st;
        ArrayList<String> temp = new ArrayList<>();
        int count = 0;
        while((str = br.readLine()) != null){
            store.original.add(str);
            temp = new ArrayList<>();
            st = new StringTokenizer(str," ");
            while(st.hasMoreTokens()){
                temp.add(st.nextToken());
            }
            if(temp.size()>0){
                store.trans.add(temp);
                if(!store.nSet.contains(temp.get(0)))
                    store.nSet.add(temp.get(0));
            }

        }

        // now order the nTerms
        String item;
        int c = 0;
        while(!store.nSet.isEmpty()){
            item = store.nSet.poll();
            store.symNum.put(item,c);
            store.numSym.put(c,item);
            c++;
        }
        System.out.println(store.trans);
        System.out.println(store.symNum);
        System.out.println(store.numSym);
        System.out.println(c);

        ArrayList<Integer> changedTrans = new ArrayList<>();
        for(int i = 0; i<c ;i++){
            for(int j = 0; j<i;j++){
                Queue<ArrayList<String>> subCreate = new LinkedList<>();
                ArrayList<String> temporary = new ArrayList<>();
                changedTrans = new ArrayList<>();
                // substitutions
                for(int pos = 0; pos < store.trans.size(); pos++){
                    ArrayList<String> tempTrans = new ArrayList<>(store.trans.get(pos));
                    String nT = store.numSym.get(i);
                    if(tempTrans.get(0).equals(nT)){
                        String searchNt = store.numSym.get(j);
                        for(int searchPos = 0; searchPos<tempTrans.size(); searchPos++){
                            if(tempTrans.get(searchPos).equals(searchNt)){
                                changedTrans.add(pos);
                                ArrayList<String> iTrans;
                                for(int iPos = 0; iPos<store.trans.size(); iPos++){
                                    iTrans = store.trans.get(iPos);
                                    temporary = new ArrayList<>(); // see this
                                    if(iTrans.get(0).equals(searchNt)){
                                        for(int cTempTrans = 0; cTempTrans<searchPos; cTempTrans++){
                                            temporary.add(tempTrans.get(cTempTrans));
                                        }
                                        for(int counter = 1; counter<iTrans.size(); counter++){
                                            temporary.add(iTrans.get(counter));
                                        }
                                        for(int cTempTrans = searchPos+1 ; cTempTrans < tempTrans.size(); cTempTrans++){
                                            temporary.add(tempTrans.get(cTempTrans));
                                        }
                                        subCreate.add(temporary);
                                        System.out.println("Temporary : " + temporary);
                                    }
                                }
                            }
                        }
                    }

                }
                while(!subCreate.isEmpty()){
                    store.trans.add(subCreate.poll());
                }

                ArrayList<ArrayList<String>> newTrans = new ArrayList<>();
                for(int k = 0; k<store.trans.size(); k++){
                    if(!changedTrans.contains(k))
                        newTrans.add(store.trans.get(k));
                }
                //System.out.println(newTrans);
                store.trans = new ArrayList<>(newTrans);
                System.out.println("Changed Trans : " + changedTrans);
                System.out.println(store.trans);



            }

            // remove the left recursion

            // Check for the left Recursion
            changedTrans = new ArrayList<>();
            //System.out.println(store.numSym.get(i) + "  : " + store.checkLeftrecur(store.numSym.get(i)));
            if(store.checkLeftrecur(store.numSym.get(i))){
                ArrayList<String> tempTrans = new ArrayList<>();
                String nT = store.numSym.get(i);
                String subs = nT+"1";
                store.newNterm.add(subs);
                Queue<ArrayList<String>> newCreate = new LinkedList<>();
                for(int pos = 0; pos<store.trans.size();pos++){
                    tempTrans = store.trans.get(pos);
                    int tempSize = tempTrans.size();
                    if(tempTrans.get(0).equals(nT)){
                        if(tempTrans.get(1).equals(nT)){
                            // direct left recursion
                            ArrayList<String> newAdd = new ArrayList<>();
                            newAdd.add(subs);
                            for(int k = 2;k<tempSize;k++){
                                newAdd.add(tempTrans.get(k));
                            }
                            newAdd.add(subs);
                            changedTrans.add(pos);
                            System.out.println(newAdd);
                            newCreate.add(newAdd);
                        }else{
                            // no left recursion
                            ArrayList<String> newAdd = new ArrayList<>();
                            newAdd.add(nT);
                            for(int k = 1;k<tempSize;k++){
                                newAdd.add(tempTrans.get(k));
                            }
                            newAdd.add(subs);
                            changedTrans.add(pos);
                            newCreate.add(newAdd);
                            System.out.println(newAdd);
                        }
                    }
                }
                while(!newCreate.isEmpty()){
                    store.trans.add(newCreate.poll());
                }
                //System.out.println(changedTrans);
                //System.out.println(store.trans);
                ArrayList<ArrayList<String>> newTrans = new ArrayList<>();
                for(int k = 0; k<store.trans.size(); k++){
                    if(!changedTrans.contains(k))
                        newTrans.add(store.trans.get(k));
                }
                //System.out.println(newTrans);
                store.trans = new ArrayList<>(newTrans);
                System.out.println(store.trans);
            }else{

            }

        }
        System.out.println("NewNterm : " + store.newNterm);
        System.out.println("Final : " + store.trans);

        // writing the output file
        for(int i = 0; i<c; i++){
            String nTerm = store.numSym.get(i);
            for(int k = 0; k<store.trans.size(); k++){
                if(store.trans.get(k).get(0).equals(nTerm)){
                    for(int j = 0; j<store.trans.get(k).size(); j++){
                        bw.write(store.trans.get(k).get(j) + " ");
                    }
                    bw.write("\n");
                }
            }
        }
        Iterator<String> iterSet = store.newNterm.iterator();
        while(iterSet.hasNext()){
            String nTerm = iterSet.next();
            for(int k = 0; k<store.trans.size(); k++){
                if(store.trans.get(k).get(0).equals(nTerm)){
                    for(int j = 0; j<store.trans.get(k).size(); j++){
                        bw.write(store.trans.get(k).get(j) + " ");
                    }
                    bw.write("\n");
                }
            }
            bw.write(nTerm + " " + "?" + "\n");
        }


        bw.close();
    }

    boolean checkLeftrecur(String nT){
        ArrayList<String> temp = new ArrayList<>();
        for(int i = 0; i< trans.size(); i++){
            temp = trans.get(i);
            if(temp.get(0).equals(nT)){
                if(temp.get(1).equals(nT))
                    return true;
            }
        }
        return false;
    }
}
