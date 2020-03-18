package vms.api.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SmsTextRepository {
	private final Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private JdbcTemplate jdbc;

	public String getMessageText(int msgCode, int langCode) {
		String query = "select msg_text from sms_text where id =" + msgCode + " and lang_code=" + langCode;
		try {
			return jdbc.queryForObject(query, String.class);
		} catch (Exception exp) {
			log.info(query + "|" + exp.getMessage());
			exp.printStackTrace();
		}
		return null;
	}
}
