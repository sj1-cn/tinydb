/*
 * This source file is subject to the license that is bundled with this package in the file LICENSE.
 */
package cn.sj1.tinydb.jdbc;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import cn.sj1.tinydb.dbal.jdbc.QueryParameters;

public class QueryParametersTest {
    @Test
    public void it_does_not_bind_parameters_if_empty_array_is_given()
        throws SQLException {
        PreparedStatement statement = mock(PreparedStatement.class);

        QueryParameters.bind(statement, new Object[]{});

        verify(statement, never()).setObject(anyInt(), anyObject());
    }

    @Test
    public void it_binds_a_single_parameter_in_array() throws SQLException {
        PreparedStatement statement = mock(PreparedStatement.class);

        QueryParameters.bind(statement, new Object[]{"luis"});

        verify(statement, times(1)).setObject(1, "luis");
    }

    @Test
    public void it_binds_several_parameters_in_array() throws SQLException {
        PreparedStatement statement = mock(PreparedStatement.class);

        QueryParameters.bind(statement, new Object[]{"luis", "montealegreluis@gmail.com", 15});

        verify(statement).setObject(1, "luis");
        verify(statement).setObject(2, "montealegreluis@gmail.com");
        verify(statement).setObject(3, 15);
    }
}
