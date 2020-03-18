package vms.api.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import vms.api.bean.ActivationConfig;

@Repository
public class ActivationConfigRepository {
	private final Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private JdbcTemplate jdbc;

	public ActivationConfig getActivationConfig(String appId, String packId) {
		String query = "select * from activation_config where appId='" + appId + "' and pack_id ='" + packId + "'";
		log.info(query);
		return jdbc.queryForObject(query, reqDataMapper);
	}

	private final RowMapper<ActivationConfig> reqDataMapper = new RowMapper<ActivationConfig>() {
		public ActivationConfig mapRow(ResultSet rs, int i) throws SQLException {
			ActivationConfig rec = new ActivationConfig();
			rec.setAppid(rs.getString("appid"));
			rec.setServiceId(rs.getString("serviceid"));
			rec.setAmount(rs.getInt("amount"));
			rec.setPackId(rs.getString("pack_id"));
			rec.setValidity(rs.getInt("validity"));
			rec.setPackName(rs.getString("pack_name"));
			return rec;
		}
	};
}
