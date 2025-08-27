package com.ccp.simple.handler;

import com.ccp.simple.domain.Role;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(Role.class)
public class RoleTypeHandler extends BaseTypeHandler<Role> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Role parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getKey());
    }

    @Override
    public Role getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String key = rs.getString(columnName);
        return getRoleByKey(key);
    }

    @Override
    public Role getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String key = rs.getString(columnIndex);
        return getRoleByKey(key);
    }

    @Override
    public Role getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String key = cs.getString(columnIndex);
        return getRoleByKey(key);
    }

    private Role getRoleByKey(String key) {
        if (key == null) {
            return null;
        }
        for (Role role : Role.values()) {
            if (role.getKey().equals(key)) {
                return role;
            }
        }
        return null;
    }
}
