package jp.loioz.dto;

import lombok.Data;

@Data
public class CustomeFolderDto {

	// フォルダSeq
	private int folderSeq;

	// フォルダ名
	private String folderName;

	// 親フォルダSeq
	private int parentFolderSeq;

}
