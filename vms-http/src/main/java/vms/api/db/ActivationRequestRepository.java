package vms.api.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import vms.api.bean.ActivationRequest;

@Repository
public class ActivationRequestRepository {

	private final Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private JdbcTemplate jdbc;

	public int deleteWithUUID(String uuid) {
		String query = "delete from activation_request where uuid = '" + uuid + "'";
		log.info(query);
		return jdbc.update(query);
	}

	public int insertIntoActivationRequest(ActivationRequest req) {
		
		String query = "insert into activation_request ( appid , msisdn , "
				+ "pack_id , amount , validity , channel , serviceid , uuid , lang_code ) values (?,?,?,?,?,?,?,?,?)";
		return jdbc.update(query, new Object[] { req.getAppid(), req.getMsisdn(), req.getPackId(), req.getAmount(),
				req.getValidity(), req.getChannel(), req.getServiceid(), req.getUuid(), req.getLang() });
		
	}

	public ActivationRequest getActivationRequest(String uuid) {
		String query = "select * from activation_request where uuid ='" + uuid + "'";
		return jdbc.queryForObject(query, reqDataMapper);
	}

	public List<ActivationRequest> getHLRActiveRequest(Date checkDate) {
		String query = "select * from activation_request where hlr_state=1 and request_time < ?";
		return jdbc.query(query, new Object[] { checkDate }, reqDataMapper);
	}

	public int updateHLRState(String msisdn, int state) {
		
		String query = "update activation_request set hlr_state = " + state + " where msisdn = '" + msisdn + "'";
		log.info(query);
		return jdbc.update(query);
		
	}

	private final RowMapper<ActivationRequest> reqDataMapper = new RowMapper<ActivationRequest>() {
		public ActivationRequest mapRow(ResultSet rs, int i) throws SQLException {
			ActivationRequest rec = new ActivationRequest();
			rec.setAppid(rs.getString("appid"));
			rec.setMsisdn(rs.getString("msisdn"));
			rec.setServiceid(rs.getString("serviceid"));
			rec.setChannel(rs.getString("channel"));
			rec.setUuid(rs.getString("uuid"));
			rec.setAmount(rs.getInt("amount"));
			rec.setPackId(rs.getString("pack_id"));
			rec.setRequestTime(rs.getTimestamp("request_time"));
			rec.setValidity(rs.getInt("validity"));
			rec.setLang(rs.getString("lang_code"));
			rec.setHlrState(rs.getInt("hlr_state"));
			return rec;
		}
	};
}
