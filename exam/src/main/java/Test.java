
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import javax.management.QueryEval;
import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class Test {
    public static void main(String[] args) {
        //获取resource下的文件
        File file = new File("E:\\download\\exam1.html");
        //解析 HTML 数据，使用Jsoup的parse()方法，解析 HTML 字符串。 该方法返回一个 HTML 文档
        Document doc = null;
        try {
            doc = Jsoup.parse(file, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println(doc);

        List<Node> nodes = doc.childNodes().get(0).childNodes().get(1).childNodes().get(0).childNodes();


        List<Question> singleChoiceList = new ArrayList<>();
        List<Question> multiChoiceList = new ArrayList<>();

        int count = 1;
        int i=3;
        while(count <= 160){
            //System.out.println(count + "=========================");
            Node x = nodes.get(i);
            Question singleChoiceQuestion = getSingleChoiceQuestion(x);
            singleChoiceList.add(singleChoiceQuestion);
            //System.out.println(singleChoiceQuestion);
            count++;
            i+=2;
        }

        // 多选题
        List<Node> nodes1 = nodes.subList(i, nodes.size());
        count = 1;
        i = 2;
        while(count <= 118){
            //System.out.println(count + "=========================");
            Node x = nodes1.get(i);
            Question multiChoiceQuestion = getMultiChoiceQuestion(x);
            multiChoiceList.add(multiChoiceQuestion);
            //System.out.println(multiChoiceQuestion);
            count++;
            i+=2;
        }


        analysis(singleChoiceList,true);


    }

    public static void analysis(List<Question> questionList,boolean isSingle){

        // 自定义比较器


        System.out.println((isSingle ? "单选题" : "多选题") + "--题量" + questionList.size());
        questionList.sort((o1,o2)-> Collator.getInstance(Locale.CHINA).compare(o1.getTitle(),o2.getTitle()));
        int count = 1;
        for (Question question : questionList) {
            System.out.println(count++ + " " + question.title);
            System.out.println(question.choice);
            System.out.println(question.answer);
        }

    }


    public static Question getMultiChoiceQuestion(Node x){
        //System.out.println();
        List<Node> nodes = x.childNode(1).childNode(1).childNodes();
        nodes = nodes.subList(2, nodes.size());
        // 题目
        StringBuilder titleStr = new StringBuilder();
        for (Node node : nodes) {
            if(node.childNodes() == null || node.childNodeSize() == 0){
                titleStr.append(node.toString());
            }else{
                titleStr.append(node.childNode(0).toString());
            }
        }

        // 选项
        StringBuilder choicesStr = new StringBuilder();
        List<Node> choicesNode = x.childNode(3).childNodes();
        for (int i = 1; i < choicesNode.size(); i+=2) {
            choicesStr.append(choicesNode.get(i).childNode(0).toString()).append(" ");
        }

        // 答案
        StringBuilder answerStr = new StringBuilder();
        Node answerNode = x.childNode(5).childNode(1).childNode(3);
        answerStr.append(answerNode.childNode(0).childNode(0)).append(answerNode.childNode(1));

        Question question = new Question();
        question.setTitle(titleStr.toString());
        question.setChoice(choicesStr.toString());
        question.setAnswer(answerStr.toString());
        return question;
    }

    public static Question getSingleChoiceQuestion(Node x){
        // 题目
        Node title = x.childNode(1).childNode(1).childNode(2);
        String titleStr = title.toString();
        // 选项
        List<Node> choices = x.childNodes().get(3).childNodes();
        StringBuilder choiceStr = new StringBuilder();
        for (int j = 1; j < 9; j+=2) {
            choiceStr.append(choices.get(j).childNode(0).toString()).append(" ");
        }

        // 答案
        Node answerNode = x.childNode(5).childNode(1).childNode(3);
        String answerStr = answerNode.childNode(0).childNode(0) +""+ answerNode.childNode(1);

//        System.out.println(titleStr);
//        System.out.println(choiceStr);
//        System.out.println(answerStr);

        Question question = new Question();
        question.setTitle(titleStr);
        question.setChoice(choiceStr.toString());
        question.setAnswer(answerStr);
        return question;
    }
}
