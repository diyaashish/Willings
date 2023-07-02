package jp.loioz.app.common.session;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@Scope(value= "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AdminSession implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long companySeq;

	private Map<String, String> uploadFileMap = new HashMap<String, String>();

	public void clear() {
		companySeq = null;
	}
}