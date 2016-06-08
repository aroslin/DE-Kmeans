package mllib;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.lang.Math;

public class Kmeans {
    public static int ClusterK =4;
    private static int genMax;
    private static int D;
    private static int NP;
    private static double F;
    private static double CR;
    private static int EvaluateType;
    private static Map<String,List<String>> Msls;
    public static void main(String[] args) throws IOException {
        double data[][] = {
                {0.1, 0.1, 0.1, 0.1, 0.2},
                {0.1, 0.2, 0.5, 0.1, 0.2},
                {0.1, 0.1, 0.2, 0.1, 0.2},
                {0.1, 0.1, 0.1, 0.2, 0.2},
                {7.1, 7.1, 7.1, 7.1, 7.2},
                {7.2, 7.5, 7.9, 7.6, 7.7},
                {7.3, 7.1, 7.5, 7.1, 7.8},
                {7.4, 7.6, 7.2, 7.1, 7.2},
                {12.1, 12.9, 12.3, 12.6, 12.2},
                {12.2, 12.3, 12.9, 12.7, 12.2},
                {12.1, 12.2, 12.9, 12.7, 12.3},
                {12.1, 12.5, 12.5, 12.2, 12.2},
                {19.5, 19.1, 19.3, 19.1, 19.2},
                {19.1, 19.3, 19.1, 19.8, 19.2},
                {19.3, 19.2, 19.1, 19.9, 19.2},
                {19.2, 19.1, 19.8, 19.4, 19.2},
                {3.1, 2.1, 5.1, 1.1, 4.2},
                {17.1, 16.1, 17.7, 17.1, 18.2},
                {10.1, 11.1, 9.1, 10.1, 8.2},
                {2.1, 3.1, 5.1, 4.1, 4.2},
                {0.0,0.0,0.0,0.0,0.0},
                {19.2,19.3,19.8,19.4,19.2},
                {5.1,6.1,7.1,8.1,9.2},
                {4.1,9.1,8.7,6.1,7.2},
                {1.1,9.1,19.1,2.1,9.2},
                {12.1,13.1,5.1,1.1,14.2},
                {9.2,8.1,1.8,8.4,7.2},
                {13.1,12.1,15.1,11.1,14.2},
                {3.1,4.1,7.7,7.1,8.2},
                {5.1,6.1,14.1,13.1,11.2},
        };
        List<Item> li=new ArrayList<>();
        for (int i=0;i<30;++i){
            Item it=new Item();
            for (int j=0;j<5;++j){
                it.label =String.valueOf(i);
                it.vec.add(data[i][j]);
            }
            li.add(it);
        }
        Msls=new HashMap<>();
        List<String> ls=Arrays.asList("林烈迎","林振轩","林晟川","林新刚","林旭亮","林昕雨","林子炫","林若屹","林秀芬","林一仪","林立平","林一呜","林芷源","林新奇","林艾朋","林聃彤","林宇","林泽腾","林洪磊","林朋","林钰丹","林海宏","林臻芩","林中哲","林芮桐","林锦","林诗瑜","林竣辰","林勋","林腾兮");
        Msls.put("姓林的",ls);
        ls=Arrays.asList("李烈迎","李振轩","李晟川","李新刚","李旭亮","李昕雨","李子炫","李若屹","李秀芬","李一仪","李立平","李一呜","李芷源","李新奇","李艾朋","李聃彤","李宇","李泽腾","李洪磊","李朋","李钰丹","李海宏","李臻芩","李中哲","李芮桐","李锦","李诗瑜","李竣辰","李勋","李腾兮");
        Msls.put("姓李的",ls);
        ls=Arrays.asList("王烈迎","王振轩","王晟川","王新刚","王旭亮","王昕雨","王子炫","王若屹","王秀芬","王一仪","王立平","王一呜","王芷源","王新奇","王艾朋","王聃彤","王宇","王泽腾","王洪磊","王朋","王钰丹","王海宏","王臻芩","王中哲","王芮桐","王锦","王诗瑜","王竣辰","王勋","王腾兮");
        Msls.put("姓王的",ls);
        ls=Arrays.asList("张烈迎","张振轩","张晟川","张新刚","张旭亮","张昕雨","张子炫","张若屹","张秀芬","张一仪","张立平","张一呜","张芷源","张新奇","张艾朋","张聃彤","张宇","张泽腾","张洪磊","张朋","张钰丹","张海宏","张臻芩","张中哲","张芮桐","张锦","张诗瑜","张竣辰","张勋","张腾兮");
        Msls.put("姓张的",ls);
        new Kmeans(4);
        DEKmeans(li);
//        TestK(li,10);
    }

    public Kmeans() {

        this.ClusterK = 4;
        this.genMax=200;
        this.D=5;
        this.NP=60;
        this.F=0.5;
        this.CR=0.9;
        this.EvaluateType=0;
    }

    public Kmeans(int i) {

        this.ClusterK = i;
        this.genMax=200;
        this.D=5;
        this.NP=60;
        this.F=0.5;
        this.CR=0.9;
        this.EvaluateType=0;
    }

    /*计算两个向量间的距离*/
    private static double getDistXY(Item tec1, Item tec2) {
        double sum = 0;
        for (int i = 0; i < tec1.vec.size(); ++i) {
            double vec1 = tec1.vec.get(i);
            double vec2 = tec2.vec.get(i);
            sum += (vec1 - vec2) * (vec1 - vec2);
        }
        return Math.sqrt(sum);
    }

    /*将某个元素聚到某一类*/
    private static int clusterOfTuple(List<Item> means, Item tuple) {
        double dist = getDistXY(means.get(0), tuple);
        double tmp;
        int label = 0;//标示属于哪一个簇
        for (int i = 1; i < ClusterK; i++) {
            tmp = getDistXY(means.get(i), tuple);
            if (tmp < dist) {
                dist = tmp;
                label = i;
            }
        }
        return label;
    }

    /*计算适应度函数*/
    private static double getVar(List<Item> clusters, List<Item> means) {
        double var = 0;
        for (int i = 0; i < clusters.size(); i++) {
            for (int j = 0; j < means.size(); j++) {
                var += getDistXY(clusters.get(i), means.get(j));
            }
        }
        //cout<<"sum:"<<sum<<endl;
        return var;
    }

    /*计算组内距离*/
    static double getEK(List<Item> clusters) {
        List<Item> means=getMeans(clusters);
        double EK = 0;
        for (Item itm : means){
            for (Item itc : clusters) {
                if (itm.clusteredClass==itc.clusteredClass)
                EK += getDistXY(itm, itc);
            }
        }
        return EK;
    }

    static double getTotalEK(List<Item> clusters) {
        Item mean = getCurrentMean(clusters);
        double EK = 0;
        for (Item itc : clusters) {
            if (mean.clusteredClass == itc.clusteredClass)
                EK += getDistXY(mean, itc);
        }
        return EK;
    }
    static double getEK(List<Item> clusters,List<Item> means) {
        double EK = 0;
        for (Item itm : means) {
            for (Item itc : clusters) {
                if (itm.clusteredClass == itc.clusteredClass)
                    EK += getDistXY(itm, itc);
            }
        }
        return EK;
    }
    static double getEK(List<Item> clusters,int k) {
        List<Item> means=getMeans(clusters);
        double EK = 0;
        int size=0;
        for (Item itc : clusters) {
            if (itc.clusteredClass == k)
            {
                EK += getDistXY(itc, means.get(k));
                size++;
            }
        }
        return EK/size;
    }

    /*计算组内距离*/
    static double getDK(List<Item> cluster) {
        List<Item> means=getMeans(cluster);
        double DK = 0;
        for (int i = 0; i < ClusterK; ++i) {
            for (int j = i + 1; j < ClusterK; ++j) {
                double tp = getDistXY(means.get(i), means.get(j));
                if (tp > DK) {
                    DK = tp;
                }
            }
        }
        return DK;
    }
    static double getDKMeans(List<Item> means) {
        double DK = 0;
        for (int i = 0; i < ClusterK; ++i) {
            for (int j = i + 1; j < ClusterK; ++j) {
                double tp = getDistXY(means.get(i), means.get(j));
                if (tp > DK) {
                    DK = tp;
                }
            }
        }
        return DK;
    }

    /*计算评价函数*/
    static double fevaluate(List<Item> clusters) {
//        return getDK(clusters) * getTotalEK(clusters)/ getEK(clusters) / ClusterK;
        return getDK(clusters)/ getEK(clusters) / ClusterK;
    }

    static double fevaluate(List<Item> clusters,List<Item> means) {
        return getDKMeans(means) / getEK(clusters,means) / ClusterK;
    }
    static double DBIndex(List<Item> clusters) {
//    static double fevaluate(List<Item> clusters) {
        List<Item> means=getMeans(clusters);
        Map<Integer,Double> mid=new HashMap<>();
        double DB=0;
        for (int i=0;i<ClusterK;++i){
            mid.put(i,getEK(clusters,i));
        }
        for (int i=0;i<ClusterK;++i){
            double ri=0;
            for (int j=i+1;j<ClusterK;++j){
                double tp= (mid.get(i)+mid.get(j))/getDistXY(means.get(i),means.get(j));
                if(tp>ri){
                    ri=tp;
                }
            }
            DB+=ri;
        }
        return ClusterK/DB ;
//        return 1/getEK(clusters,means);
    }

    static double SilhouetteCoefficient(List<Item> clusters){
//    static double fevaluate(List<Item> clusters) {
        double Sout=0;
        for (Item item:clusters){
            double a_i=AverageDist(clusters,item,item.clusteredClass);
            List<Double> b_is=new ArrayList<>();
            for (int j=0; j < ClusterK; ++j) {
                if (j != item.clusteredClass) {
                    double tp = AverageDist(clusters, item, j);
                    b_is.add(tp);
                }
            }
            double b_i=Collections.min(b_is);
            Sout+=((b_i-a_i)/Math.max(a_i,b_i));
        }
        return Sout/clusters.size();
    }

    private static double AverageDist(List<Item> clusters,Item item,int k){
        double dist=0;
        int num=0;
        for (Item item2:clusters){
            if ((k==item2.clusteredClass)&&(!item.label.equals(item2.label))){
                dist+=getDistXY(item,item2);
                num++;
            }
        }
        dist=dist/num;
        return dist;
    }

    /*得到所有的聚类中心*/
    static private List<Item> getMeans(List<Item> clusters) {
        List<Item> means=new ArrayList<>();
        for (int i = 0; i < ClusterK; i++) //更新每个簇的中心点
        {
            List<Item> currentCluster = new ArrayList<>();
            for (int j = 0; j < clusters.size(); j++) {
                if (clusters.get(j).clusteredClass == i)
                    currentCluster.add(clusters.get(j));
            }
            Item item = getCurrentMean(currentCluster);
            item.clusteredClass = i;
            means.add(item);
        }
        return means;
        //cout<<"sum:"<<sum<<endl;
    }

    /*得到当前类的中心*/
    static private Item getCurrentMean(List<Item> currentCluster) {
        int num = currentCluster.size();
        Item it = new Item();
        Map<Integer, Double> mid = new HashMap<>();
        int dimNum = currentCluster.get(0).vec.size();
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < dimNum; ++j) {
                if (mid.containsKey(j)) mid.put(j, mid.get(j) + currentCluster.get(i).vec.get(j));
                else mid.put(j, currentCluster.get(i).vec.get(j));
            }
        }
        for (int j = 0; j < dimNum; ++j)
            it.vec.add(mid.get(j) / num);
        return it;
    }

    /*模型*/
    public static List<Item> model(List<Item> tuples) throws IOException {
        List<Item> clusters = new ArrayList<Item>();//k个簇
        List<Item> means = new ArrayList<Item>();//k个中心点
        //一开始随机选取k条记录的值作为k个簇的质心（均值）
        List<Integer> ks = new ArrayList<>();
        for (int i = 0; i < ClusterK; ) {
            int iToSelect = (int) (Math.random() * (tuples.size() - 1));
            if (!ks.contains(iToSelect)) {
                Item item = tuples.get(iToSelect);
                item.clusteredClass = i;
                means.add(item);
                ++i;
            }
            ks.add(iToSelect);
        }
        int lable = 0;
        //根据默认的质心给簇赋值
        for (int i = 0; i < tuples.size(); ++i) {
            lable = clusterOfTuple(means, tuples.get(i));
            Item it = tuples.get(i);
            it.clusteredClass = lable;
            clusters.add(it);
        }
        double oldVar = -1;
        double newVar = getVar(clusters, means);
        System.out.print("初始的整体误差平方和为：" + newVar + "\n");
        int t = 0;
        while (Math.abs(newVar - oldVar) >= 1e-10) //当新旧函数值相差不到1即准则函数值不发生明显变化时，算法终止
        {
            System.out.print("第 " + t + " 次迭代开始：\n");
            means=getMeans(clusters);
            oldVar = newVar;
//            newVar = getVar(clusters, means); //计算新的准则函数值
            newVar = fevaluate(clusters);
            clusters.clear();


            //根据新的质心获得新的簇
            for (int i = 0; i != tuples.size(); ++i) {
                lable = clusterOfTuple(means, tuples.get(i));
                Item it = tuples.get(i);
                it.clusteredClass = lable;
                clusters.add(it);
            }
            System.out.print("此次迭代之后的EK为：" + getEK(clusters) + "\n");
            System.out.print("此次迭代之后的DK为：" + getDK(clusters) + "\n");
            ++t;
        }
        System.out.print("最终的聚类中心：\n");
        for (int i = 0; i< ClusterK; ++i){
            System.out.print(String.format("means%d[" ,i));
            for (Item it:means){
                if (it.clusteredClass==i){
                    for (int kk=0;kk<it.vec.size();++kk){
                        System.out.print(String.format("%5.1f," ,it.vec.get(kk)));
                    }
                    System.out.print("\n");
                }
            }
        }
        System.out.print("聚类结果：\n");
        for (int i = 0; i< ClusterK; ++i){
            System.out.print(String.format("class%d\n[" ,i));
            for (Item it:clusters){
                if (it.clusteredClass==i){
                    System.out.print(it.label +"\t");
                    for (int kk=0;kk<it.vec.size();++kk){
                        System.out.print(String.format("%5.1f," ,it.vec.get(kk)));
                    }
                    System.out.print("\n");
                }
            }
        }
        LiWrite(clusters,means,"E:\\result.txt");
        return clusters;
    }
    public static void LiWrite(List<Item> clusters, List<Item> means,String filename) throws IOException {
        try {
//            String outpath = "E:\\Result.txt";
            File outfile = new File(filename);
            if (!outfile.exists())
                outfile.createNewFile();
            FileOutputStream out = new FileOutputStream(outfile, false); //如果追加方式用true
//            for (Item i:means){
//                StringBuffer sb = new StringBuffer();
//                for (Item j:clusters){
//                    if (i.clusteredClass==j.clusteredClass){
//                        int dist=(int)getDistXY(i,j)*10;
//                        sb.append(j.label+"\t"+dist + "\n" );
//                    }
//                }
//                sb.append("\n" );
//                out.write(sb.toString().getBytes("utf-8"));
//            }
//            for (int i=0;i<means.size();++i){
//                for (int j=i+1;j<means.size();++j){
//                    StringBuffer sb = new StringBuffer();
//                    int dist=(int)getDistXY(means.get(i),means.get(j))*10;
//                    sb.append(means.get(i).clusteredClass+"\t"+means.get(j).clusteredClass+"\t"+dist + "\n" );
//                    out.write(sb.toString().getBytes("utf-8"));
//                }
//            }

            StringBuffer sb = new StringBuffer();
            for (Item i : clusters) {
                sb = new StringBuffer();
                int classIndex=0;
                for (Map.Entry<String, List<String>> msl : Msls.entrySet()) {
                    if (i.clusteredClass==classIndex){
                        for (Item j:means){
                            if (i.clusteredClass==j.clusteredClass){
                                sb.append(msl.getValue().get(Integer.valueOf(i.label)) + "\t"+j.clusteredClass+"\t"+getDistXY(i,j)+"\n");
                            }
                        }
                    }
                    classIndex++;
                }
                out.write(sb.toString().getBytes("utf-8"));
            }
            for (int i=0;i<means.size();++i){
                for (int j=i+1;j<means.size();++j){
                    sb = new StringBuffer();
                    int dist=(int)getDistXY(means.get(i),means.get(j));
                    sb.append(means.get(i).clusteredClass+"\t"+means.get(j).clusteredClass+"\t"+dist + "\n" );
                    out.write(sb.toString().getBytes("utf-8"));
                }
            }
//            sb.append("\n");
//            out.write(sb.toString().getBytes("utf-8"));
//            for (Item i : clusters) {
//                StringBuffer sb1 = new StringBuffer();
//                int classIndex=0;
//                for (Map.Entry<String, List<String>> msl : Msls.entrySet()) {
//                    if (i.clusteredClass==classIndex){
//                        sb1.append("\""+msl.getKey() + "\",");
//                    }
//                    classIndex++;
//                }
//                out.write(sb1.toString().getBytes("utf-8"));
//            }
            out.close();
        } catch (Exception e) {

        }
    }

    public static List<Item> TestK(List<Item> li,int MaxK){
        List<Item> liOut=new ArrayList<>();
        double Fevaluate=0;
        int SuitableK=0;
        for (int i=1;i<=MaxK;++i){
            new Kmeans(i);
            List<Item> tp=DEKmeans(li);
            double IFevaluate=fevaluate(tp);
            if (IFevaluate>Fevaluate){
                liOut.clear();
                for (Item item:tp){
                    liOut.add(item.clone());
                    SuitableK=i;
                }
            }
        }
        li.clear();
        li=liOut;
        System.out.print(SuitableK+"\n\n");
        ClusterK=SuitableK;
        System.out.print(ClusterK+"类聚类结果：\n");
        List<Item> means=getMeans(liOut);
        for (int i = 0; i< ClusterK; ++i){
            System.out.print(String.format("class%d\n" ,i));
            System.out.print("Mean:"+"\t");
            for (int kk=0;kk<means.get(i).vec.size();++kk){
                System.out.print(String.format("\t%5.1f" ,means.get(i).vec.get(kk)));
            }
            System.out.print("\n");
            for (Item it:li){
                if (it.clusteredClass==i){
                    System.out.print(it.label +"\t");
                    for (int kk=0;kk<it.vec.size();++kk){
                        System.out.print(String.format("\t%5.1f," ,it.vec.get(kk)));
                    }
                    System.out.print("\n");
                }
            }
        }
        return li;
    }
    /*差分演化算法优化的Kmeans算法
    * genMax--最高迭代次数，D--数据维数，NP--种群数量，
    * F--变异因子，CR--交叉概率因子*/
    public static List<Item> DEKmeans(List<Item> li){
        List<List<Item>> oldGen=new ArrayList<>();
        List<List<Item>> newGen=new ArrayList<>();
        List<Item> bestMeans=new ArrayList<>();
        List<Double> Evaluates=new ArrayList<>();
        double emax=0;
        /*生成初始种群*/
        for (int i=0;i<NP;++i)
        {
            List<Item> means=RandomMeans(li);
            oldGen.add(means);
            for (Item item:means){
                bestMeans.add(item.clone());
            }
        }
        /*生成初始评价*/
        for (int i=0;i<NP;++i)
        {
            List<Item> tp=oldGen.get(i);
            li=NewCluster(li,tp);
            Evaluates.add(fevaluate(li));
        }
        for (int i=0;i<NP;++i)
        {
            newGen.add(oldGen.get(i));
            if (Evaluates.get(i)>emax){
                emax=Evaluates.get(i);
            }
        }

        /*进化开始，终止条件有待修改！！！！！！*/
        for (int gen=0;(gen<genMax)&&(Math.abs(emax)>1e-10);++gen){
            /*更新种群*/
            for (int i=0;i<NP;++i){
                List<Item> oldMeans=oldGen.get(i);
                /*生成新的簇*/
                li=NewCluster(li,oldMeans);
                List<Item> newMeans=getMeans(li);
                li=NewCluster(li,newMeans);
                /*评价更新，如果结果较好，增更新bestMeans*/
                double trial_energy=fevaluate(li);
                if (trial_energy>Evaluates.get(i)){
                    Evaluates.set(i,trial_energy);
                    if (trial_energy>emax){
                        emax=trial_energy;
                        bestMeans.clear();
                        for (Item item:newMeans){
                            bestMeans.add(item.clone());
                        }
                    }
                }
                else {
                    for (Item item:oldGen.get(i)){
                        newMeans.add(item.clone());
                    }
                }
                newGen.add(newMeans);
            }
            /*更新种群*/
            oldGen.clear();
            oldGen=DE(newGen,bestMeans);
            newGen.clear();
        }
        li=NewCluster(li,bestMeans);
        List<Item> currentMeans=getMeans(li);
//        System.out.print(ClusterK+"类聚类结果：\n");
//        for (int i = 0; i< ClusterK; ++i){
//            System.out.print(String.format("class%d\n" ,i));
//            System.out.print("Mean:"+"\t");
//            for (int kk=0;kk<bestMeans.get(i).vec.size();++kk){
//                System.out.print(String.format("%5.1f," ,bestMeans.get(i).vec.get(kk)));
//            }
//            System.out.print("\n");
//            for (Item it:li){
//                if (it.clusteredClass==i){
//                    System.out.print(it.label +"\t");
//                    for (int kk=0;kk<it.vec.size();++kk){
//                        System.out.print(String.format("%5.1f," ,it.vec.get(kk)));
//                    }
//                    System.out.print("\n");
//                }
//            }
//        }
        System.out.print("EK：\t"+getEK(li)+"\n");
        System.out.print("DK：\t"+getDK(li)+"\n");
        System.out.print("fevaluate：\t"+fevaluate(li)+"\n\n");
        return li;
    }

    /*生成随机聚类中心*/
    private static List<Item> RandomMeans(List<Item> li){
        List<Item> means=new ArrayList<>();
        int r;
        List<Integer> rs=new ArrayList<>();
        for (int i=0;i<ClusterK;)
        {
            boolean flag=false;
            r = (int)(Math.random()*li.size());
            if (rs.contains(r)) {
                flag = true;
            }
            if (!flag)
            {
                Item item=new Item();
                Item tpItem=li.get(r);
                for (double d:tpItem.vec){
                    item.vec.add(d);
                }
                item.label=tpItem.label;
                item.clusteredClass=i;
                means.add(item);
                rs.add(r);
                ++i;
            }
        }
        return means;
    }

    /*生成新的簇*/
    private static List<Item> NewCluster(List<Item> oldCluster,List<Item> means){
        List<Item> clusters=new ArrayList<>();
        for(int j=0;j<oldCluster.size();++j){
            Item item=new Item();
            Item tpItem=oldCluster.get(j);
            for (double d:tpItem.vec){
                item.vec.add(d);
            }
            item.label=tpItem.label;
            item.clusteredClass=clusterOfTuple(means,item);
            clusters.add(item);
        }
        /*如果出现空簇，则选择离某个聚类中心最远的点到空簇去
        * 当然，如果这个点本身对应的类就只有一个点，那就不要改了...*/
        List<Integer> lnum= EmptyClusters(clusters);
        int changeNum=0;
        Set<Integer> ChangeList = new HashSet<>();
        for (int num:lnum){
            Map<Integer,Integer> mii=ClusterSize(clusters);
            double dist=0;
            for (int i=0;i<ClusterK;++i){
                Item mean=means.get(i);
                for (int j=0;j<clusters.size();++j){
                    Item tuple=clusters.get(j);
                    double tpDist=getDistXY(mean,tuple);
                    if (tpDist>dist && (!ChangeList.contains(j)) && (mii.get(tuple.clusteredClass)>1)){
                        dist=tpDist;
                        changeNum=j;
                    }
                }
            }
            clusters.get(changeNum).clusteredClass=num;
            ChangeList.add(changeNum);
        }
        List<Integer> lnum2= EmptyClusters(clusters);
        return  clusters;
    }

    /*差分演化，策略 /best/2/exp*/
    private static List<List<Item>> DE(List<List<Item>> newGen,final List<Item> bestMeans){
        List<List<Item>> tpGen=new ArrayList<>();
        for (int i=0;i<NP;++i){
            List<Item> tpMeans;
            int r1=0,r2=0,r3=0,r4=0;
            do{
                // Endless loop for NP < 2 !!!
                r1=(int)(Math.random()*NP);
            }while (r1==i);
            do{
                // Endless loop for NP < 3 !!!
                r2=(int)(Math.random()*NP);
            }while ((r2 == i) || (r2 == r1));
            do
            {
                // Endless loop for NP < 4 !!!
                r3 = (int)(Math.random()*NP);
            } while ((r3 == i) || (r3 == r1) || (r3 == r2));

            do
            {
                // Endless loop for NP < 5 !!!
                r4 = (int)(Math.random()*NP);
            } while ((r4 == i) || (r4 == r1) || (r4 == r2) || (r4 == r3));
            tpMeans=new ArrayList<>();
            for (Item item:newGen.get(i))  {
                tpMeans.add(item);
            }
            int n=(int)(Math.random()*ClusterK);
            int L=0;
            do{
                /*头一遭觉得List没有数组好用...*/
                List<Double> ld=new ArrayList<>();
                for(int j=0;j<D;++j){
                    double bset=bestMeans.get(n).vec.get(j);
                    double r1Value=newGen.get(r1).get(n).vec.get(j);
                    double r2Value=newGen.get(r2).get(n).vec.get(j);
                    double r3Value=newGen.get(r3).get(n).vec.get(j);
                    double r4Value=newGen.get(r4).get(n).vec.get(j);
                    double tpd=bset+(r1Value+r2Value-r3Value-r4Value)*F;
                    ld.add(tpd);
                }
                tpMeans.get(n).vec=ld;
                n=(n+1)%ClusterK;
                ++L;
            } while ((Math.random() < CR) && (L < ClusterK));
            tpGen.add(tpMeans);
        }
        return tpGen;
    }

    private static List<Integer> EmptyClusters (List<Item> li){
        List<Integer> lint=new ArrayList<>();
        for (Item i :li){
            if (!lint.contains(i.clusteredClass)) lint.add(i.clusteredClass);
        }
        List<Integer> lintOut=new ArrayList<>();
        for (int i = 0; i < ClusterK; ++i) {
            if (!lint.contains(i))
                lintOut.add(i);
        }
        return lintOut;
    }
    private static Map<Integer,Integer> ClusterSize(List<Item> clusters){
        Map<Integer,Integer> mii=new HashMap<>();
        for (int i=0;i<ClusterK;++i){
            int size=0;
            for (Item item:clusters){
                if (item.clusteredClass==i) size++;
            }
            mii.put(i,size);
        }
        return mii;
    }
}