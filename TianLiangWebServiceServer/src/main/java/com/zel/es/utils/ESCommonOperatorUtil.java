package com.zel.es.utils;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 * ES公共操作工具类
 * 
 * @author zel
 * 
 */
public class ESCommonOperatorUtil {
	private JestClientFactory factory = new JestClientFactory();
	private String server_ip;
	private int data_port;

	public int getData_port() {
		return data_port;
	}

	public void setData_port(int data_port) {
		this.data_port = data_port;
	}

	private int admin_port;

	// 传入ip、port、构建client创建工厂类
	public ESCommonOperatorUtil(String es_index_server_ip, int data_port,
			int admin_port) {
		this.server_ip = es_index_server_ip;
		this.data_port = data_port;
		this.admin_port = admin_port;
		
		HttpClientConfig.Builder builder = new HttpClientConfig.Builder(
				StaticValue.prefix_http + es_index_server_ip + ":" + data_port);
		builder.readTimeout(SystemParas.jest_client_timeout);
		factory.setHttpClientConfig(builder.multiThreaded(true).build());
		// factory.setHttpClientConfig(new HttpClientConfig.Builder(
		// StaticValue.prefix_http + es_index_server_ip + ":" + data_port)
		// .multiThreaded(true).build());
	}

	/**
	 * 获取一个jest的对象
	 * 
	 * @return
	 */
	public JestClient getJestClient() {
		JestClient client = factory.getObject();
		return client;
	}

	/**
	 * es自带的管理端口9300的client
	 * 
	 * @return
	 */
	public Client getEsClient() {
		// 配置你的es,现在这里只配置了集群的名,默认是elasticsearch,跟服务器的相同,如果这里不相同则代表要启动不同的client，会出现各种sniff的问题
		Settings settings = ImmutableSettings.settingsBuilder()
				.put("cluster.name", SystemParas.es_cluster_name).build();
		// 这里可以同时连接集群的服务器,可以多个,并且连接服务是可访问的
		Client client = null;
		client = new TransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(server_ip,
						admin_port));
//		client.close();
		return client;
	}

	public static void main(String[] args) {
		ESCommonOperatorUtil esCommonOperatorUtil = new ESCommonOperatorUtil(
				null, 0, 0);
		Client client = esCommonOperatorUtil.getEsClient();
		// JestClient client = getJestClient();
		QueryBuilder query = QueryBuilders.termQuery("dt", "20140505");

//		client.prepareDeleteByQuery(SystemParas.tanx_bid_log_indexName)
//				.setQuery(query).execute().actionGet();
		client.close();
		// client.shutdownClient();
		System.out.println("client---" + client);
		System.out.println("执行完成!");
	}
}
