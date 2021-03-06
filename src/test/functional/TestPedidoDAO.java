package test.functional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import pedido.model.Pagamento;
import pedido.model.PagamentoDAO;
import pedido.model.Pedido;
import pedido.model.PedidoDAO;
import test.mock.PedidoMock;
import util.ConnectionFactory;
import cliente.model.Cliente;
import cliente.model.ClienteDAO;

public class TestPedidoDAO {
	private static Cliente cliente;
	private static Pagamento pagamento;
	private static String testeCreatePk;
	private static Pedido pedido;
	public static Connection con;
	
	@BeforeClass
	public static void beforeTests() throws SQLException{
		
		con = ConnectionFactory.getConnection();
		
		cliente = new ClienteDAO(con).retrieve(new Cliente(null, null, null, null, null, null));
		pagamento = new PagamentoDAO(con).retrieve(new Pagamento(null, null)).iterator().next();
		pedido = new Pedido(cliente,pagamento);
		
		String sql = "INSERT INTO pedido (codigo, cliente_fk, forma_de_pagamento_fk, data_hora) values (?,?,?,?)";
		PreparedStatement statement = prepareStatement(sql);
		
		statement.executeUpdate();
		
	}

	@Test
	public void testPedidoCreate() throws SQLException{
		
		Cliente clienteCreate = new Cliente(null, "teste", "teste", "teste", "teste", "teste");
		Pagamento pagamentoCreate = new Pagamento(null, "Dinheiro");
		PedidoMock pedidoTest = new PedidoMock(clienteCreate, pagamentoCreate);
		new PedidoDAO(con).create(pedidoTest);
		
		String sql = "SELECT * FROM pedido WHERE codigo = ?";
		PreparedStatement pStatement = con.prepareStatement(sql);
		pStatement.setString(1, pedidoTest.getCodigo());
		ResultSet result = pStatement.executeQuery();
		
		
		assertEquals(true, result.next());
		
	}
	
	@Test
	public void testPedidoRetrieve(){
		
		Pedido pedidoBanco = new PedidoDAO(con).retrieve(pedido).iterator().next();
		
		assertEquals(pedidoBanco.getCodigo(), pedido.getCodigo());
		
	}
	
	@AfterClass
	public static void limpaDados() throws SQLException{
		
		String sql = "DELETE FROM pedido WHERE codigo = ? AND cliente_fk = ? AND forma_de_pagamento_fk = ? AND data_hora = ?";
		PreparedStatement statement = prepareStatement(sql);
		statement.executeUpdate();
		
	}
	
	private static PreparedStatement prepareStatement(String sql) throws SQLException{
		PreparedStatement statement = con.prepareStatement(sql);
		
		statement.setString(1, pedido.getCodigo());
		statement.setInt(2, cliente.getCodigo());
		statement.setInt(3, pagamento.getCodigo());
		statement.setTimestamp(4, pedido.getDataHora());
		return statement;
	}
}
