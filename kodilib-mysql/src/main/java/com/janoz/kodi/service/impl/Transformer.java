package com.janoz.kodi.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.LocalDate;

import com.janoz.tvapilib.model.Art;
import com.janoz.tvapilib.model.impl.Episode;
import com.janoz.tvapilib.model.impl.Show;
import com.janoz.kodi.model.KodiArt;
import com.janoz.kodi.model.KodiArt.MediaType;
import com.janoz.kodi.model.KodiEpisode;
import com.janoz.kodi.model.KodiFile;
import com.janoz.kodi.model.KodiGenre;
import com.janoz.kodi.model.KodiSeason;
import com.janoz.kodi.model.KodiTvShow;

public class Transformer {

	
	public static KodiEpisode fromEpisode(Episode episode, KodiSeason season, KodiFile file) {
		KodiEpisode result = new KodiEpisode();
		//idShow
		result.setSeason(season);
		//idFile
		result.setFile(file);
		//c00	 Episode Title
		result.setTitle(episode.getTitle());
		//c01	 Plot Summary
		result.setPlot(episode.getDescription());
		//c03	 Rating
		result.setRating(episode.getRating());
		//c04	 Writer
		//c05	 First Aired
		result.setFirstAired(new LocalDate(episode.getAired()));
		//c09	 Episode length in seconds
		//c10	 Director
		//c13	 Episode Number
		result.setEpsiode(episode.getEpisode());
		return result;
	}
	
	public static KodiTvShow fromShow(Show show) {
		KodiTvShow result = new KodiTvShow();
		//c00 Titel
		result.setTitle(show.getTitle());
		//c01 omschrijving
		result.setPlot(show.getDescription());
		//c04 rating
		result.setRating(show.getRating());
		//c05 firstaired
		result.setFirstAired(LocalDate.fromDateFields(show.getSeason(1).getEpisode(1).getAired()));
		//c08 genre
		for (String genre : show.getGenres()) {
			KodiGenre xbmcGenre = KodiGenre.getByNameOrSynonym(genre);
			if (xbmcGenre != null) {
				result.getGenres().add(xbmcGenre);
			}
		}
		
		//c12 thetvdbid
		result.setTvdbId(show.getTheTvDbId());
		//c13 content rating
		result.setContentRating(show.getContentRating());
		//c14 network
		result.setNetwork(show.getNetwork());
		//c17 pathId waar show is
		//todo
		return result;
	}

	private static Map<Art.Type,KodiArt.Type> artTypeMap = new HashMap<Art.Type, KodiArt.Type>();
	static {
		artTypeMap.put(Art.Type.BACKDROP, KodiArt.Type.FANART);
		artTypeMap.put(Art.Type.BANNER, KodiArt.Type.BANNER);
		artTypeMap.put(Art.Type.CLEARART, KodiArt.Type.CLEARART);
		artTypeMap.put(Art.Type.CLEARLOGO, KodiArt.Type.CLEARLOGO);
		artTypeMap.put(Art.Type.POSTER, KodiArt.Type.POSTER);
		artTypeMap.put(Art.Type.THUMB, KodiArt.Type.LANDSCAPE);//behalve bij episode
	}
	
	public static KodiArt fromArt(Art art, MediaType type, int mediaId) {
		KodiArt result = new KodiArt();
		result.setMediaId(mediaId);
		result.setMediaType(type);
		result.setUrl(art.getUrl());
		result.setType(artTypeMap.get(art.getType()));
		if ((type == MediaType.EPISODE) && (art.getType() == Art.Type.THUMB)) {
			result.setType(KodiArt.Type.THUMB); //TODO maybe nicer!
		}
		return result;
	}

}
