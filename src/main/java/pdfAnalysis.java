import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class pdfAnalysis {
    public static void main(String[] args) throws IOException {
        File docFile = new File("F:\\workspace\\pdf\\document\\toSee.doc");
        docFile.delete();
        pdfAnalysis pdf = new pdfAnalysis();
        String pdfName = "F:\\workspace\\pdf\\document\\信管试卷.pdf";
        String pdf_Body = pdf.readFileOfPDF(pdfName);
        File pdfFile = new File("F:\\workspace\\pdf\\document\\pdf.txt");
        File txtFile = new File("F:\\workspace\\pdf\\document\\toSee.txt");
        PrintWriter printWriter = new PrintWriter(pdfFile);
        printWriter.write("");
        printWriter.write(pdf_Body);
        PrintWriter txtWriter = new PrintWriter(txtFile);
        txtWriter.println();
        txtWriter.println();
        txtWriter.println();
        transformToText(pdf_Body, txtWriter);
        txtFile.renameTo(new File("F:\\workspace\\pdf\\document\\toSee.doc"));
        printWriter.close();
        pdfAnalysis.infile.close();
    }

    public static void transformToText(String pdf, PrintWriter writer) {
        String remove="信管网(www.cnitpm.com)：最专业信息系统项目管理师、系统集成项目管理工程师网站";
        pdf=pdf.replace(remove,"");
        String[] arrays = pdf.split("[信][管][网][参][考][答][案]");
        arrays[0] = arrays[0].replaceAll("[\\s\\S]*1、", "1、");
        List keyList = new ArrayList();
        for (int i = 1; i < arrays.length-10; i++) {
            String keyArea = arrays[i];
            Pattern p = Pattern.compile("[A-Z]");
            Matcher matcher = p.matcher(keyArea);
            if (matcher.find()) {
                keyList.add(i - 1, matcher.group());
            }
        }
        for(int i=0;i<arrays.length-11;i++){
            String area = arrays[i];
            area=area.replaceAll("[A-Z]\\s*.*\\s*(\\d+、)+","");
            int endindex=area.indexOf("信管网解析");
            area=area.substring(0,endindex);

            //标题
            String question=area.split("A、")[0];
            //答案
            String key=keyList.get(i).toString();

            //选项
            String choice="";
            String choiceRegex=key+"、.*";
            Pattern pattern=Pattern.compile(choiceRegex);
            Matcher matcher=pattern.matcher(area);
            if(matcher.find()){
                choice=matcher.group().substring(2);
                choice=choice.replaceAll("\\s*|\\t|\\r|\\n","");
            }
            question=question.replaceAll("\\s*|\\t|\\r|\\n","");
            String document=question.replace("（）","【"+choice+"】");

            if (i==0){
                document=document.substring(2);
            }
            else {
                document=document.substring(1);
            }
            if (!document.contains("【")){
                continue;
            }


            writer.append(document);
            writer.println();
            writer.println();
        }
        writer.close();
    }

    public static FileInputStream infile = null;

    public String readFileOfPDF(String pdfName) throws IOException {
        String context = null;
        File file = new File(pdfName);// 创建一个文件对象


        try {
            infile = new FileInputStream(pdfName);// 创建一个文件输入流
            // 新建一个PDF解析器对象
            PDFParser parser = new PDFParser(infile);
            // 对PDF文件进行解析
            parser.parse();
            // 获取解析后得到的PDF文档对象
            PDDocument pdfdocument = parser.getPDDocument();
            // 新建一个PDF文本剥离器
            PDFTextStripper stripper = new PDFTextStripper();
            // 从PDF文档对象中剥离文本
            context = stripper.getText(pdfdocument);
            // System.out.println(context);

        } catch (Exception e) {
            System.out.println("读取PDF文件" + file.getAbsolutePath() + "失败！" + e.getMessage());
        } finally {

            if (infile != null) {
                try {
                    infile.close();
                } catch (IOException e1) {
                }
            }
        }
        return context;

    }
}
