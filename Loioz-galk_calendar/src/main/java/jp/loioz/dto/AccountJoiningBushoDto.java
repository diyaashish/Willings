package jp.loioz.dto;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Entity
@Data
public class AccountJoiningBushoDto {

	@Column(name = "account_seq")
	private Long accountSeq;

	@Column(name = "account_name")
	private String accountName;

	@Column(name = "busho_id")
	private String belongingBusho;
}
