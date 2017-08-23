/**
 * 创建于 2017年8月16日 下午1:10:10
 * @author zhg
 */
package familia;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import familia.ModelConfig.ModelType;
import xiaogen.util.Logger;

/**
 * 主题模型模型存储结构，包含词表和word topic count两分布
 * 其中LDA和SentenceLDA使用同样的模型存储格式
 */ 
public class TopicModel
{
	static class TopicCount implements Comparable<TopicCount>
	{
		int topic_id;
		int count;
		@Override
		public int compareTo(TopicCount o)
		{
			return topic_id-o.topic_id;
		} 
	}

	static class TopicDist extends ArrayList<TopicCount>
	{
		/** */
		private static final long serialVersionUID = 1L;

	}

	// 模型对应的词表数据结构
	Vocab _vocab;
	// word topic 模型参数
	Map<Integer, TopicDist> _word_topic;
	// word topic对应的每一维主题的计数总和
	Map<Integer, Integer> _topic_sum;
	// 主题数
	public int _num_topics;
	// 主题模型超参数
	public float _alpha;
	float _alpha_sum;
	float _beta;
	float _beta_sum;
	// 模型类型
	ModelType _type;

	/**
	 * @param model_dir
	 * @param config
	 */
	public TopicModel(String model_dir, ModelConfig config)
	{
		_num_topics = config.num_topics;
		_beta = config.beta;
		_alpha = config.alpha;
		_alpha_sum = _alpha * _num_topics;
		_topic_sum = new HashMap<>(_num_topics);
		_type = config.type;
		_vocab = new Vocab();
		// 加载模型
		load_model(model_dir + "/" + config.word_topic_file, model_dir + "/" + config.vocab_file);
	}

	int term_id(String term)
	{
		return _vocab.get_id(term);
	}

	// 加载word topic count以及词表文件
	void load_model(String word_topic_path, String vocab_path)
	{
		_vocab.load(vocab_path);
		_beta_sum = _beta * _vocab.size();
		_word_topic = new HashMap<>(_vocab.size()); 
		load_word_topic(word_topic_path); 
		
	}

	void load_word_topic(String word_topic_path)
	{
		Logger.d("加载 word topic 模型: " + word_topic_path);
		try
		{
			Files.lines(Paths.get(word_topic_path)).forEach(line -> {
				String[] fields = line.split(" ");
				assert fields.length > 0;
				int term_id = Integer.parseInt(fields[0]);
				for (int i = 1; i < fields.length; ++i)
				{
					String[] topic_count = fields[i].split(":");
					int topic_id = Integer.parseInt(topic_count[0]);
					int count = Integer.parseInt(topic_count[1]);
					{
						TopicDist list = _word_topic.get(term_id);
						if (list == null)
						{
							list = new TopicDist();
						}
						TopicCount e = new TopicCount();
						e.topic_id = topic_id;
						e.count = count;
						list.add(e);
						_word_topic.put(term_id, list);
					}
					if (!_topic_sum.containsKey(topic_id))
					{
						_topic_sum.put(topic_id, count);
					} else
					{
						int _count = _topic_sum.get(topic_id) + count;
						_topic_sum.put(topic_id, _count);
					}
				}
				// 按照主题下标进行排序 
				_word_topic.get(term_id).sort(null); 
			});
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		Logger.d("word topic 主题数量:"+_word_topic.size());
	}

	// 返回模型中某个词在某个主题下的参数值，由于模型采用稀疏存储，若找不到则返回0
	int word_topic(int word_id, int topic_id)
	{
		TopicDist ts = _word_topic.get(word_id);
		if(ts!=null)
		{
			for(TopicCount t:ts)
			{
				if(t.topic_id==topic_id)
				{
					return t.count;
				}
			}
		} 
		return 0; 
	}
	  int vocab_size()   {
	        return _vocab.size();
	    }
	// 返回某个词的主题分布
	TopicDist word_topic(int term_id)
	{
		return _word_topic.get(term_id);
	}

	// 返回指定topic id的topic sum参数
	int topic_sum(int topic_id)
	{
		return _topic_sum.get(topic_id);
	}
}
