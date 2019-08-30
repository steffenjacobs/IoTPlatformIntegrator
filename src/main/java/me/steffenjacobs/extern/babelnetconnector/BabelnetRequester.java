package me.steffenjacobs.extern.babelnetconnector;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.steffenjacobs.extern.babelnetconnector.domain.Synset;
import me.steffenjacobs.extern.babelnetconnector.domain.SynsetDescription;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingKey;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingService;

public class BabelnetRequester {
	private static final Logger LOG = LoggerFactory.getLogger(BabelnetRequester.class);

	// add some lifo cache
	private Map<String, Map<String, Synset>> cache;

	private final SettingService settingService;

	public BabelnetRequester(SettingService settingService) {
		this.settingService = settingService;

	}

	public Map<String, Synset> requestSynsets(String word, BabelLanguage searchLanguage, BabelLanguage targetLanguage) {

		if (cache == null) {
			this.cache = new HashMap<>();
		} else if (cache.containsKey(word)) {
			return cache.get(word);
		}

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Synset> synSets = new HashMap<>();

		try {
			List<SynsetDescription> readValue = mapper.readValue(new URL("https://babelnet.io/v5/getSynsetIds?lemma=" + word + "&searchLang=" + searchLanguage.getKey()
					+ "&targetLang=" + targetLanguage.getKey() + "&key=" + settingService.getSetting(SettingKey.BABELNET_API_KEY)), new TypeReference<List<SynsetDescription>>() {
					});
			readValue.forEach(synsetDescr -> {
				synsetDescr.getId();
				try {

					final Synset synset = mapper.readValue(new URL("https://babelnet.io/v5/getSynset?id=" + synsetDescr.getId() + "&searchLang=" + searchLanguage.getKey()
							+ "&targetLang=" + targetLanguage.getKey() + "&key=e73aa086-6d60-4a61-ba57-08a6185358b5"), Synset.class);

					if (synset.getAdditionalProperties().containsKey("message") && synset.getAdditionalProperties().get("message").toString().startsWith("Your key is not valid or the daily requests limit has been reached")) {
						LOG.warn("Daily usage limit for Babelnet API reached.");
					} else {
						synSets.put(synsetDescr.getId(), synset);
					}
				} catch (IOException e) {
					LOG.error("Cannot deserialize synset {}: {} ", synsetDescr.getId(), e.getMessage());
				}
			});

		} catch (IOException e1) {
			LOG.error(e1.getMessage(), e1);
		}

		return synSets;

	}
}
