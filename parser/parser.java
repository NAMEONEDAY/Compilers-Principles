import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Node;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;




public class parser  {
	//终结符
	public static int LP=0;// {
	public static int RP=1;// }
	public static int LB=2;// )
	public static int RB=3;// )
	public static int INT=4;
	public static int CHAR=5;
	public static int VOID=6;
	public static int RETURN=7;
	public static int GiG=8;
	public static int EQUAL=9;
	public static int ADD=10;
	public static int SUB=11;
	public static int MUL=12;
	public static int DIV=13;
	public static int GjG=14;
	public static int IF=15;
	public static int ELSE=16;
	public static int WHILE=17;
	public static int EQUAL2=18;
	public static int BIG=19;
	public static int BIGE=20;
	public static int LES=21;
	public static int LESE=22;
	public static int NE=23;
	public static int SEM=24;
	
	//终结符
	public static String VT[]= {
			"{","}","(",")","int","char","void","return","GiG","=","+","-","*","/","GjG","if","else"
			,"while","==",">",">=","<","<=","!=",";","#"
	};
	//非终结符
	public static String VN[]= {"CMPL_UNIT","FUNC_LIST","FUNC_DEF","TYPE_SPEC","PARA_LIST","PARA_LIST2","ARGUMENT","CODE_BLOCK","STMT_LIST",
			"STMT","RTN_STMT","ASSIGN_STMT","EXPR","EXPR2","TERM","TERM2","FACTOR","JUDGE_STMT","JUDGE_LIST",
			"JUDGE_LIST2","BRANCH_STMT","LOOP_STMT","DECL_STMT","JUDGE","ASSIGN2"};
	//LL分析表
	public static String[][] LL=new String[25][26];
	//LL初始化
	public static void makell() {
		LL[0][INT]="FUNC_LIST";
		LL[0][CHAR]="FUNC_LIST";
		LL[0][VOID]="FUNC_LIST";
		LL[1][VOID]="FUNC_DEF FUNC_LIST";
		LL[1][INT]="FUNC_DEF FUNC_LIST";
		LL[1][CHAR]="FUNC_DEF FUNC_LIST";
		LL[1][25]="emptyempty";
		LL[2][INT]="TYPE_SPEC GiG ( PARA_LIST ) CODE_BLOCK";
		LL[2][VOID]="TYPE_SPEC GiG ( PARA_LIST ) CODE_BLOCK";
		LL[2][CHAR]="TYPE_SPEC GiG ( PARA_LIST ) CODE_BLOCK";
		LL[3][INT]="int";
		LL[3][VOID]="void";
		LL[3][CHAR]="char";
		LL[4][INT]="ARGUMENT PARA_LIST2";
		LL[4][VOID]="ARGUMENT PARA_LIST2";
		LL[4][CHAR]="ARGUMENT PARA_LIST2";
		LL[4][RB]="emptyempty";
		LL[5][INT]=", ARGUMENT PARA_LIST2";
		LL[5][VOID]=", ARGUMENT PARA_LIST2";
		LL[5][CHAR]=", ARGUMENT PARA_LIST2";
		LL[5][RB]="emptyempty";
		LL[6][INT]="TYPE_SPEC GiG";
		LL[6][VOID]="TYPE_SPEC GiG";
		LL[6][CHAR]="TYPE_SPEC GiG";
		LL[7][LP]="{ STMT_LIST }";
		LL[8][RP]="emptyempty";
		LL[8][GiG]="STMT STMT_LIST";
		LL[8][INT]="STMT STMT_LIST";
		LL[8][CHAR]="STMT STMT_LIST";
		LL[8][VOID]="STMT STMT_LIST";
		LL[8][GjG]="STMT STMT_LIST";
		LL[8][LP]="STMT STMT_LIST";
		LL[8][IF]="STMT STMT_LIST";
		LL[8][WHILE]="STMT STMT_LIST";
		LL[8][RETURN]="STMT STMT_LIST";
		LL[9][RETURN]="RTN_STMT";
		LL[9][GiG]="ASSIGN_STMT";
		LL[9][IF]="BRANCH_STMT";
		LL[9][WHILE]="LOOP_STMT";
		LL[9][INT]="DECL_STMT";
		LL[9][CHAR]="DECL_STMT";
		LL[9][VOID]="DECL_STMT";
		LL[10][RETURN]="return EXPR ;";
		LL[11][GiG]="GiG = EXPR ;";
		LL[12][GiG]="TERM EXPR2";
		LL[12][GjG]="TERM EXPR2";
		LL[12][LB]="TERM EXPR2";
		LL[13][ADD]="+ TERM EXPR2";
		LL[13][SUB]="- TERM EXPR2";
		LL[13][SEM]="emptyempty";
		LL[13][NE]="emptyempty";
		LL[13][LESE]="emptyempty";
		LL[13][LES]="emptyempty";
		LL[13][BIGE]="emptyempty";
		LL[13][BIG]="emptyempty";
		LL[13][EQUAL2]="emptyempty";
		LL[13][RB]="emptyempty";
		LL[14][GiG]="FACTOR TERM2";
		LL[14][GjG]="FACTOR TERM2";
		LL[14][LB]="FACTOR TERM2";
		LL[15][MUL]="* FACTOR TERM2";
		LL[15][DIV]="/ FACTOR TERM2";
		LL[15][ADD]="emptyempty";
		LL[15][SUB]="emptyempty";
		LL[15][SEM]="emptyempty";
		LL[15][NE]="emptyempty";
		LL[15][LESE]="emptyempty";
		LL[15][LES]="emptyempty";
		LL[15][BIGE]="emptyempty";
		LL[15][BIG]="emptyempty";
		LL[15][EQUAL2]="emptyempty";
		LL[15][RB]="emptyempty";
		LL[16][GiG]="GiG";
		LL[16][GjG]="GjG";
		LL[16][LB]="( EXPR )";
		LL[17][GiG]="JUDGE_LIST";
		LL[17][GjG]="JUDGE_LIST";
		LL[17][LB]="JUDGE_LIST";
		LL[18][GiG]="EXPR JUDGE_LIST2";
		LL[18][GjG]="EXPR JUDGE_LIST2";
		LL[18][LB]="EXPR JUDGE_LIST2";
		LL[19][EQUAL2]="JUDGE EXPR";
		LL[19][BIG]="JUDGE EXPR";
		LL[19][BIGE]="JUDGE EXPR";
		LL[19][LES]="JUDGE EXPR";
		LL[19][LESE]="JUDGE EXPR";
		LL[19][NE]="JUDGE EXPR";
		LL[19][RB]="emptyempty";
		LL[20][IF]="if ( JUDGE_STMT ) CODE_BLOCK else CODE_BLOCK";
		LL[21][WHILE]="while ( JUDGE_LIST ) CODE_BLOCK";
		LL[22][INT]="TYPE_SPEC GiG ASSIGN2 ;";
		LL[22][CHAR]="TYPE_SPEC GiG ASSIGN2 ;";
		LL[22][VOID]="TYPE_SPEC GiG ASSIGN2 ;";
		LL[23][EQUAL2]="==";
		LL[23][BIG]=">";
		LL[23][BIGE]=">=";
		LL[23][LES]="<";
		LL[23][LESE]="<=";
		LL[23][NE]="!=";
		LL[24][EQUAL]="= EXPR";
		LL[24][SEM]="emptyempty";
	}
	
	public static int getVN(String s) {
		int i;
		for (i=0;i<VN.length;++i) {
			if(s.equals(VN[i]))
				break;
		}
		return i;
	}
	
	public static int getVT(String s) {
		int i;
		for (i=0;i<VT.length;++i) {
			if(s.equals(VT[i]))
				break;
		}
		return i;
	}
	//余留输入串value
	public static List<String> stack1=new ArrayList();
	public static List<String> stack2=new ArrayList();
	public static List<String> stack3=new ArrayList();
	
	public static void xmltostr(String iFile) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		File file=new File(iFile);
		Document doc = db.parse(file);
		NodeList tokenList = doc.getElementsByTagName("token");
		for(int i=0;i<tokenList.getLength();++i) {
			stack1.add((doc.getElementsByTagName("value").item(i).getFirstChild().getNodeValue()));
			stack2.add((doc.getElementsByTagName("type").item(i).getFirstChild().getNodeValue()));
		}
	}
	
	static void run(String iFile, String oFile) throws ParserConfigurationException, SAXException, IOException {
		makell();
		xmltostr(iFile);
		DocumentBuilderFactory f=DocumentBuilderFactory.newInstance();
		DocumentBuilder b=f.newDocumentBuilder();
		Document doc=b.newDocument();
		Element pro=doc.createElement("project");
		pro.setAttribute("name", "test.c");
		doc.appendChild(pro);
		stack3.add("CMPL_UNIT");
		Element ee=doc.createElement("CMPL_UNIT");
		pro.appendChild(ee);
		fenxi(ee,doc);
		saveXML(doc,oFile);
	}
	
	private static void saveXML(Document doc,String oFile) {
		TransformerFactory transFactory=TransformerFactory.newInstance();
		try {
			Transformer transformer=transFactory.newTransformer();
			transformer.setOutputProperty("indent", "yes");
			
			DOMSource source=new DOMSource();
			source.setNode(doc);
			StreamResult result=new StreamResult(oFile);
			
			transformer.transform(source, result);
		}
		catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }   
	}

	private static void fenxi(Element pro, Document doc) throws ParserConfigurationException {
		System.out.println(stack1);
		System.out.println(stack2);
		System.out.println(stack3);
		if(!stack1.isEmpty()) {
			int i=25;
			i=getVT(stack2.get(0));
			if(i>24)
				i=getVT(stack1.get(0));
			int j=26;
			j=getVN(stack3.get(stack3.size()-1));
			if(j==VN.length) {
				if(stack3.get(stack3.size()-1).equals(stack1.get(0))||stack3.get(stack3.size()-1).equals(stack2.get(0))) {
					pro.setTextContent(String.valueOf(stack1.get(0)));
					stack3.remove(stack3.size()-1);
					stack1.remove(0);
					stack2.remove(0);
				}
			}
			else {
				String str=LL[j][i];
				stack3.remove(stack3.size()-1);
				int begin=stack3.size();
				String[] arr= {};
				/*System.out.println(j);
				System.out.println(i);*/
				if(!LL[j][i].equals("emptyempty")){
					arr = str.split(" ");
				}
				int f;
				for(f=arr.length-1;f>=0;f--) {
					stack3.add(arr[f]);
				}
				int h;
				System.out.println(stack3);
				for(h=stack3.size()-1;h>=begin;h--) {
					System.out.println(h);
					System.out.println(begin);
					System.out.println(stack3.get(h));
					int CC=26;
					CC=getVN(stack3.get(h));
					Element val3;
					if(CC<VN.length) {
						val3=doc.createElement(stack3.get(h));
						pro.appendChild(val3);
					}
					else {
						val3=doc.createElement(stack2.get(0));
						pro.appendChild(val3);
					}
					fenxi(val3,doc);
				}
			}
			
		}
	}

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		run(args[0], args[1]);
	}

}
