package org.thunlp.thulac.postprocess;

import org.thunlp.thulac.data.Dat;
import org.thunlp.thulac.data.DatMaker;
import org.thunlp.thulac.data.TaggedWord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PostprocessPass implements IPostprocessPass {
	private Dat p_dat;
	private String tag;

	public PostprocessPass(String filename, String tag, boolean isTxt) throws
			IOException {
		this.tag = tag;
		if (isTxt) this.p_dat = DatMaker.readFromTxtFile(filename);
		else this.p_dat = new Dat(filename);
	}

	@Override
	public void process(List<TaggedWord> sentence) {
		if (this.p_dat == null) return;
		if (sentence.isEmpty()) return;

		List<String> tmp = new ArrayList<>();
		for (int i = 0; i < sentence.size(); i++) {
			TaggedWord tagged = sentence.get(i);
			StringBuilder sb = new StringBuilder(tagged.word);
			if (this.p_dat.getInfo(sb.toString()) >= 0) continue;

			tmp.clear();
			for (int j = i + 1; j < sentence.size(); j++) {
				sb.append(sentence.get(j).word);
				if (this.p_dat.getInfo(sb.toString()) >= 0) break;
				tmp.add(sb.toString());
			}

			int k = tmp.size() - 1;
			for (; k >= 0 && this.p_dat.match(tmp.get(k)) == -1; k--) ;
			if (k >= 0) {
				sb.setLength(0);
				for (int j = i; j < i + k + 2; j++) sb.append(sentence.get(j).word);
				tagged.word = sb.toString();
				tagged.tag = this.tag;

				for (int j = i + k + 1; j > i; j--) sentence.remove(j);
			}
		}
	}
}
