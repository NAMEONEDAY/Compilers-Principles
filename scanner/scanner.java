import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class Scanner {
	
	final static int START=0;
	final static int KEYWORD=48;
	final static int ID=49;
	final static int CONST=7;
	final static int SP=29;
	final static int OP=47;
	
	private static String[] KeyWords = new String[]{
			"auto","if","unsigned","break","inline","void","case","int","volatile","char","long",
			"while","const","register","_Alignas","continue","restrict","_Alignof","default","return",
			"_Atomic","do","short","_Bool","double","signed","_Complex","else","sizeof","_Generic","enum",
			"static","_Imaginary","extern","struct","_Noreturn","float","switch","_Static_assert","for","typedef",
			"_Thread_local","goto","union","unix"
	};

	private static String[] Special = {"{","}","[","]","(",")","#",",",".",";",":","...","->","<:",":>","<%","%>","%:","%:%:"}; // 特殊字符

	
	static BufferedReader sourceFile;
	final static int BUF_SIZE = 1024*1024;
	static int bufSize=0;
	static String eachLine;
	static char[] LineBuf = new char[BUF_SIZE];
	static int lineNum=0;
	static int charPos=0;
	static boolean isEOF=false;
	
	static char getNextChar() throws Exception{
		try{
				char nextChar = 0;
				if(charPos>=bufSize){
					if((eachLine = sourceFile.readLine())!= null){
						lineNum++;
						LineBuf = eachLine.toCharArray();
						bufSize = eachLine.length();
						charPos = 0;
						nextChar = LineBuf[charPos++];
					}
					else{
						isEOF = true;
						nextChar = '@';
					}
				}
				else{
					nextChar = LineBuf[charPos++];
				}
				return nextChar;
				}catch (Exception e) {
			//		System.out.println(charPos);
				// TODO: handle exception
				}
				return 0;
	}
	
	//是否为16进制
	static boolean isH(char c) {
		if (('a'<=c&&c<='f')||('A'<=c&&c<='F')||('0'<=c&&c<='9')) {
			return true;
		}
		else return false;
	}
	
	//是否8进制
	static boolean isO(char c) {
		if ('0'<c&&c<'7') {
			return true;
		}
		else return false;
	}
	
	//是否10进制
	static boolean isD(char c) {
		if ('0'<c&&c<'9') {
			return true;
		}
		else return false;
	}
	
	//是否为字母，下划线
	static boolean isK(char c) {
		if (('a'<=c&&c<='z')||('A'<=c&&c<='Z')||(c=='_')) 
			return true;
		else return false;
	}
	
	static boolean isSP(String s) {
		int len=Special.length;
		for (int i=0;i<len;++i) 
			if(s.equals(Special[i]))
				return true;
		return false;
	}
	
	static boolean isKey(String s) {
		int len=KeyWords.length;
		for (int i=0;i<len;++i) 
			if(s.equals(KeyWords[i]))
				return true;
		return false;
	}
	
	static void scanner(String oFile) throws Exception {
		DocumentBuilderFactory f=DocumentBuilderFactory.newInstance();
		DocumentBuilder b=f.newDocumentBuilder();
		Document doc=b.newDocument();
		Element pro=doc.createElement("project");
		pro.setAttribute("name", "test.c");
		Element tks=doc.createElement("tokens");
		doc.appendChild(pro);
		pro.appendChild(tks);
		int i=0;
		String tokenStr = "";
		String currentToken = "";
		int currentState = START;
		boolean shSave = false;
		char c;
		while((c=getNextChar())!=-1&&!isEOF) {
			shSave=false;
			switch(currentState) {
			case START:
				if(c=='0')
					currentState=8;
				else if(isD(c)) 
					currentState=3;
				else if(c=='\'')
					currentState=17;
				else if(c=='u'||c=='U'||c=='L')
					currentState=16;
				else if(isK(c))
					currentState=1;
				else if(c=='/')
					currentState=33;
				else if(c=='+')
					currentState=34;
				else if(c=='-')
					currentState=35;
				else if(c=='&')
					currentState=36;
				else if(c=='*')
					currentState=37;
				else if(c=='~')
					currentState=38;
				else if(c=='!')
					currentState=39;
				else if(c=='^')
					currentState=41;
				else if(c=='%')
					currentState=42;
				else if(c=='|')
					currentState=43;
				else if(c=='<')
					currentState=44;
				else if(c=='>')
					currentState=45;
				else if(c=='=')
					currentState=46;
				else if(c=='"')
					currentState=19;
				else if(c==' '||c=='\n'||c=='\t'||c=='\r') 
					shSave=true;
				else
					currentState=29;
				break;
			case 1:
				if(isD(c))
					currentState=1;
				else if(isK(c))
					currentState=1;
				else if(c==' '||c=='\n'||c=='\t'||c=='\r') {
					shSave=true;
					currentState=2;
				}
				else
					currentState=29;
				break;
			case 3:
				if(isD(c))
					currentState=1;
				else if(c==' '||c=='\n'||c=='\t'||c=='\r') {
					shSave=true;
					currentState=CONST;
				}
				else if(c=='.')
					currentState=4;
				else
					currentState=29;
				break;
			case 4:
				if(isD(c))
					currentState=4;
				else if(c==' '||c=='\n'||c=='\t'||c=='\r') 
					shSave=true;
				else if(c=='E'||c=='e')
					currentState=5;
				else
					currentState=29;
				break;
			case 5:
				if(c=='+'||c=='-'||isD(c))
					currentState=6;
				else if(c==' '||c=='\n'||c=='\t'||c=='\r') 
					shSave=true;
				else
					currentState=29;
				break;
			case 6:
				if(isK(c))
					currentState=6;
				else if(c==' '||c=='\n'||c=='\t'||c=='\r') {
					shSave=true;
					currentState=CONST;
				}
				else
					currentState=29;
				break;
			case 8:
				if(c=='x')
					currentState=9;
				else if(isO(c)&&c!='0')
					currentState=11;
				else if(c==' '||c=='\n'||c=='\t'||c=='\r') {
					shSave=true;
					currentState=CONST;
					}
				else
					currentState=29;
				break;
			case 9:
				if(isH(c))
					currentState=9;
				else if(c==' '||c=='\n'||c=='\t'||c=='\r') {
					shSave=true;
					currentState=CONST;
				}
				else
					currentState=29;
				break;
			case 11:
				if(isO(c))
					currentState=11;
				else if(c==' '||c=='\n'||c=='\t'||c=='\r') {
					shSave=true;
					currentState=CONST;
				}
				else
					currentState=29;
				break;
			case 13:
				if(c=='\''||c=='\"'||c=='?'||c=='\\'||c=='a'||c=='b'||c=='f'||c=='n'||c=='r'||c=='t'||c=='v')
					currentState=18;
				else if(c==' '||c=='\n'||c=='\t'||c=='\r') 
					shSave=true;
				else
					currentState=29;
				break;
			case 16:
				if(c=='8')
					currentState=16;
				else if(c=='\'')
					currentState=17;
				else if(c==' '||c=='\n'||c=='\t'||c=='\r') 
					shSave=true;
				else
					currentState=29;
				break;
			case 17:
				if(c=='\\')
					currentState=13;
				else if(c==' '||c=='\n'||c=='\t'||c=='\r') 
					shSave=true;
				else
					currentState=18;
				break;
			case 18:
				if(c=='\'')
					currentState=15;
				else if(c==' '||c=='\n'||c=='\t'||c=='\r') 
					shSave=true;
				else
					currentState=29;
				break;
			case 15:
				if(c==' '||c=='\n'||c=='\t'||c=='\r') {
					shSave=true;
					currentState=CONST;
				}
				else
					currentState=29;
				break;
			case 19:
				if(!(c=='"'))
					currentState=50;
				break;
			case 50:
				if(c=='"')
					currentState=21;
				else
					currentState=50;
				break;
			case 21:
				if(c==' '||c=='\n'||c=='\t'||c=='\r') {
					currentState=CONST;
					shSave=true;
				}
				else
					currentState=29;
				break;
			case 33:
				if(c==' '||c=='\n'||c=='\t'||c=='\r') {
					currentState=OP;
					shSave=true;
				}
				else if(c=='=')
					currentState=40;
				else
					currentState=29;
				break;
			case 34:
				if(c==' '||c=='\n'||c=='\t'||c=='\r') {
					currentState=OP;
					shSave=true;
				}
				else if(c=='='||c=='+')
					currentState=40;
				else
					currentState=29;
				break;
			case 35:
				if(c==' '||c=='\n'||c=='\t'||c=='\r') {
					currentState=OP;
					shSave=true;
				}
				else if(c=='='||c=='-')
					currentState=40;
				else
					currentState=29;
				break;
			case 36:
				if(c==' '||c=='\n'||c=='\t'||c=='\r') {
					currentState=OP;
					shSave=true;
				}
				else if(c=='&')
					currentState=40;
				else
					currentState=29;
				break;
			case 37:
				if(c==' '||c=='\n'||c=='\t'||c=='\r') {
					currentState=OP;
					shSave=true;
				}
				else if(c=='=')
					currentState=40;
				else
					currentState=29;
				break;
			case 38:
				if(c==' '||c=='\n'||c=='\t'||c=='\r') {
					currentState=OP;
					shSave=true;
				}
				else
					currentState=29;
				break;
			case 39:
				if(c==' '||c=='\n'||c=='\t'||c=='\r') {
					currentState=OP;
					shSave=true;
				}
				else if(c=='=')
					currentState=40;
				else
					currentState=29;
				break;
			case 41:
				if(c==' '||c=='\n'||c=='\t'||c=='\r') {
					currentState=OP;
					shSave=true;
				}
				else if(c=='=')
					currentState=40;
				else
					currentState=29;
				break;
			case 42:
				if(c==' '||c=='\n'||c=='\t'||c=='\r') {
					currentState=OP;
					shSave=true;
				}
				else if(c=='=')
					currentState=40;
				else
					currentState=29;
				break;
			case 43:
				if(c==' '||c=='\n'||c=='\t'||c=='\r') {
					currentState=OP;
					shSave=true;
				}
				else if(c=='='||c=='|')
					currentState=40;
				else
					currentState=29;
				break;
			case 44:
				if(c==' '||c=='\n'||c=='\t'||c=='\r') {
					currentState=OP;
					shSave=true;
				}
				else if(c=='=')
					currentState=40;
				else if(c=='<')
					currentState=51;
				else
					currentState=29;
				break;
			case 45:
				if(c==' '||c=='\n'||c=='\t'||c=='\r') {
					currentState=OP;
					shSave=true;
				}
				else if(c=='=')
					currentState=40;
				else if(c=='>')
					currentState=51;
				else
					currentState=29;
				break;
			case 46:
				if(c==' '||c=='\n'||c=='\t'||c=='\r') {
					currentState=OP;
					shSave=true;
				}
				else if(c=='=')
					currentState=40;
				else
					currentState=29;
				break;
			case 51:
				if(c==' '||c=='\n'||c=='\t'||c=='\r') {
					currentState=OP;
					shSave=true;
				}
				else if(c=='=')
					currentState=40;
				else
					currentState=29;
				break;
			case 40:
				if(c==' '||c=='\n'||c=='\t'||c=='\r') {
					currentState=OP;
					shSave=true;
				}
				else
					currentState=29;
				break;
			case 29:
				if(c==' '||c=='\n'||c=='\t'||c=='\r') 
					shSave=true;
				else
					currentState=29;
				break;
			}
			if(!shSave)
				tokenStr+=c;
			/*System.out.println(shSave);
			System.out.println(c);
			System.out.println(currentState);
			System.out.println(tokenStr);
			System.out.println("\n");*/
			if(shSave){
				if(tokenStr.length()>0) {
					i++;
					Element tk=doc.createElement("token");
					tks.appendChild(tk);
					Element num=doc.createElement("number");
					num.setTextContent(String.valueOf(i));
					tk.appendChild(num);
					Element val=doc.createElement("value");
					val.setTextContent(String.valueOf(tokenStr));
					Element type=doc.createElement("type");
					if(currentState==CONST) 
						type.setTextContent("const");
					else if(currentState==OP) 
						type.setTextContent("operator");
					else if(currentState==2) {
						if(isKey(tokenStr))
							type.setTextContent("keyword");
						else
							type.setTextContent("identifier");
					}
					else
						if(isSP(tokenStr))
							type.setTextContent("separtor");
						else
							type.setTextContent("NO");
					tk.appendChild(type);
					Element ln=doc.createElement("line");
					ln.setTextContent(String.valueOf(lineNum));
					tk.appendChild(ln);
					Element vild=doc.createElement("valid");
					vild.setTextContent("true");
					tk.appendChild(vild);
				}
				tokenStr="";
				currentState=START;
			}
		}
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

	static void runsc(String iFile,String oFile) throws IOException,Exception{
		sourceFile=new BufferedReader(new FileReader(iFile));
		scanner(oFile);
	}
	
	public static void main(String[] args) throws IOException, Exception {
		runsc(args[0],args[1]);
		}
}
