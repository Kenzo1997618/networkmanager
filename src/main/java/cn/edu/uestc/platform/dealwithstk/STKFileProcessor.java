package cn.edu.uestc.platform.dealwithstk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;

import cn.edu.uestc.platform.pojo.LinkForFilter;

public class STKFileProcessor {

	// 根据传来的文件名路径，在当前路径下生成新的文件
	public String STKFileCreater(String path) throws IOException {
		String filepath = path.substring(0,path.lastIndexOf("/")+1)+"Changed"+path.substring(path.lastIndexOf("/")+1,path.length());
		File file = new File(filepath);
		file.setWritable(true);
		file.setReadable(true);
		file.createNewFile();
		FileOutputStream fileOutputStream = new FileOutputStream(file);

		// 为创建的新文件添加第一行
		String str = "\"Time (EpMin)\",\"Strand Name\",\"Range (km)\"";
		byte[] buf = str.getBytes();
		fileOutputStream.write(buf);
		fileOutputStream.write("\r\n".getBytes());
		fileOutputStream.close();
		// 返回生成的文件名
		return filepath;
	}
	


	/*
	 * 给生成的新文件写入过滤后的文件context
	 */
	public void STKFileRewrite(String path, String newPath, List<LinkForFilter> filterRegulation) throws IOException {
		// File file = new File(path);

		Reader in = new FileReader(path);
		File newFile = new File(newPath);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		FileOutputStream fileOutputStream = new FileOutputStream(newFile, true);
		//
		for (CSVRecord record : records) {
			// record.get
			// fileOutputStream.write(record.toString().getBytes());
			String fromNodeName = record.get("Strand Name").substring(record.get("Strand Name").indexOf("/") + 1,
					record.get("Strand Name").indexOf(" "));
			String toNodeName = record.get("Strand Name").substring(record.get("Strand Name").lastIndexOf("/") + 1,
					record.get("Strand Name").length());
			int flag = 0;
			for (LinkForFilter l : filterRegulation) {
				// 如果这条记录与过滤中的记录有匹配的 此条记录不写，直接break;
				if (((fromNodeName.equals(l.getFromNodeName())) && ((toNodeName.equals(l.getToNodeName()))))
						|| ((fromNodeName.equals(l.getToNodeName())) && ((toNodeName.equals(l.getFromNodeName()))))) {
					flag = 1;
					break;
				}
			}
			// 若没有过滤此条记录 重写这条记录到新文件
			if (flag == 0) {
				String time = record.get("Time (EpMin)");
				String StrandName = "\""+record.get("Strand Name")+"\"";
				String Range = record.get("Range (km)");
				String rcd = time + "," + StrandName + "," + Range;
				fileOutputStream.write(rcd.getBytes());
				fileOutputStream.write("\r\n".getBytes());
			}
		}
		fileOutputStream.close();
	}

}
