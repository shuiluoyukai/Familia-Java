/**
 * 创建于 2017年8月15日 下午5:55:57
 * @author zhg
 */
package familia.demo;

import java.util.ArrayList;
import java.util.List;
import familia.InferenceEngine;
import familia.LDADoc;
import familia.SemanticMatching;
import familia.SimpleTokenizer;
import familia.Tokenizer;
import familia.Topic;
import familia.TopicalWordEmbedding;

/**
 * 
 */
public class QueryDocSimDemo extends Demo
{
	InferenceEngine _engine;
	// Topic Word Embedding模型
	TopicalWordEmbedding _twe;
	// 分词器
	Tokenizer _tokenizer;

	/**
	 * @param model_dir
	 * @param conf_file
	 * @param model_dir2
	 * @param emb_file
	 */
	public QueryDocSimDemo(String model_dir, String conf_file, String emb_file)
	{
		_engine = new InferenceEngine(model_dir, conf_file);
		_twe = new TopicalWordEmbedding(model_dir, emb_file);
		_tokenizer = new SimpleTokenizer(model_dir + "/vocab_info.txt");
	}

	// 计算query (短文本) 与 document (长文本) 的相似度
	// 可选的指标包括:
	// 1. document主题分布生成query的likelihood, 值越大相似度越高
	// 2. 基于TWE模型的相似度计算
	void cal_query_doc_similarity(String query, String document)
	{
		// 分词
		;
		List<String> q_tokens = _tokenizer.tokenize(query);
		List<String> d_tokens = _tokenizer.tokenize(document);
		print_tokens("Query Tokens", q_tokens);
		print_tokens("Doc Tokens", d_tokens);

		// 对长文本进行主题推断，获取主题分布
		LDADoc doc = new LDADoc();
		_engine.infer(d_tokens, doc);
	
		List<Topic> doc_topic_dist = new ArrayList<>();
		doc.sparse_topic_dist(doc_topic_dist);

		float lda_sim = SemanticMatching.likelihood_based_similarity(q_tokens, doc_topic_dist, _engine.get_model());
		float twe_sim = SemanticMatching.twe_based_similarity(q_tokens, doc_topic_dist, _twe);

		System.out.println("LDA Similarity = " + lda_sim);
		System.out.println("TWE Similarity = " + twe_sim);
	}

	// 打印分词结果
	void print_tokens(String title, List<String> tokens)
	{
		System.out.println(title+":" );
		System.out.println(tokens);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{ 
		String model_dir = getModelDir() + "/news";
		String conf_file = "lda.conf";
		String emb_file = "news_twe_lda.model"; 
		QueryDocSimDemo qd_sim_demo = new QueryDocSimDemo(model_dir, conf_file, emb_file);
		// 计算短文本与长文本的相似度
		getConsole(br -> 
		{
			System.out.println("请输入短文本:");
			String query =br.nextLine();
			System.out.println("请输入长文本:");
			String doc =br.nextLine();
			qd_sim_demo.cal_query_doc_similarity(query, doc);   
		});
	}

}
