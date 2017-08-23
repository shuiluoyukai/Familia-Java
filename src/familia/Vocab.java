/**
 * 创建于 2017年8月16日 下午1:20:15
 * @author zhg
 */
package familia;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import xiaogen.util.Logger;

/**
 * 
 */
// 主题模型词表数据结构
// 主要负责明文单词到词id之间的映射, 若单词不在词表中，则范围OOV(-1)
public class Vocab
{
	// OOV: out of vocabulary, 表示单词不在词表中
	final static int OOV = -1;
	// 明文到id的映射
	Map<String, Integer> _term2id = new HashMap<>();

	/** 
	 * 范围给定明文单词的词id 
	 * **/
	int get_id(String word)
	{
		Integer id = _term2id.get(word);
		if (id == null)
		{
			return OOV;
		} else
		{
			return id;
		}
	}

	// 加载词表
	void load(String vocab_file)
	{
		Logger.d("加载词表:" + vocab_file);
		Logger.st();
		_term2id.clear();
		try
		{
			Files.lines(Paths.get(vocab_file)).forEach(line -> {
				String[] term_id = line.split("\t");
				assert term_id.length == 5;
				int id = Integer.parseInt(term_id[2]);
				if (_term2id.containsKey(term_id[1]))
				{
					Logger.d("重复单词:" + term_id[1]);
					return;
				}
				_term2id.put(term_id[1], id);
			});
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		Logger.t("词库数量:" + size());
	}

	// 返回词表大小
	int size()
	{
		return _term2id.size();
	}
}
