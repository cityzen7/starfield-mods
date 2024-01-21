# Say My Name

Make more companions say your name!

I love how Vasco can say 1000 player names. It's such a neat immersion booster. When I saw all the recent developments in AI, I thought it would be cool to use AI to have more characters say your name. To do the project, I wanted to find an AI that was free and could be run locally (not in the cloud).

The goal of this mod is to feature nearly all the same player names as Vasco says in the game, with the possibility of adding names by request, for a period of time. (See Requests and bug reports below).

There will be different download options for different names, and I'll create a new mod for each new character (and link to them in the descriptions).

I'll add a video in the video section for a preview of the voice, and you can see all the lines that are changed by looking at the text files on gihub: https://github.com/ManApart/starfield-mods/blob/master/tools/reference/sayMyName/

## Limits

Because this is generated locally, the quality isn't the best on the market. (At this scale, something like using eleven labs would be expensive and time consuming).

I hope people will enjoy this mod and find it immersive, and not just hate on the sound quality. I'm splicing the name into the existing voice line, and there is a clear quality difference between the name and the rest of the line. If you're not paying attention, or in a busy soundscape, I think it does the job, but it's clearly not perfect. If it's not high enough quality for you, please don't waste your time complaining and instead build something better.

Until the Creation Kit is out, I can't update the lip sync files or dialogue text, so the name and any words after the name won't be lip synced or show the name in text. Once the creation kit comes out, I hope to mass generate lip sync files, but there is no guarantee I'll be able to do that in a scalable way.

## Future Plans

- I plan to work through names alphabetically and may publish the mod before the full alphabet is complete. In that case, I plan to continue uploading packs of the OG names
- If the mod is well received, for some period of time I'll upload additional packs of requested names
- Again if the mod is well received, I plan to do additional characters, starting with constellation and _possibly_ branching out to other crew


## Installation

Names will be broken up into folders alphabetically, so you don't have to download a too-massive file just for your player name. Installation requires a manual step, even if using a mod manager. Navigate to `<mod-folder>/Data/sound/voice/starfield.esm/<npc-name>/` and then find the folder with the player name you want. Copy all the files inside that folder to the <npc> folder, and then delete all the name folders. If you don't do this step, you won't hear your voice lines!

If you've done it correctly, your mod folder (or Data folder in the game) should look like: `Data/sound/voice/starfield.esm/npcfandreja/00ae4a02.wem`, with a bunch of wem files.


## How This Was Made

All the source code is available on github. While it's hard coded to my use case, it's possible to adjust it to your own workflow if you wanted to do something similar.
https://github.com/ManApart/starfield-mods/blob/master/tools/src/main/kotlin/readme.md

Basically, I'm using https://coqui.ai/ running locally to generate each line I want, cloning the NPC voice but inserting a given player name. (Generating most of the original line gives the AI the context so it says the name with generally the right inflection). Then I crop out the name using an automated process that crops based on silences around the name. Then I stitch that name into the original line, which I've manually split into prefix and suffix, and normalize the volume.

One the new line is created, I copy it over to WWise to turn the wav into a wem, and then copy it back, remove the wwise filename suffix, and place it in the mod staging folder.

While the process is highly automated, there are still a number of manual touch points, and the line generation itself takes quite a few hours per group of names.

## Making Requests and Reporting Bugs

If this mod is positively received, I'm considering doing requests for people. That said, I have some stringent constraints to prevent my own burnout.

For both requests for new names as well as bug reports, please use the comments section.

Please note that if any of these criteria are not met, I intend to not even respond to your post, again, for my own burnout prevention.

If you don't like these constraints, you can check out the source code on github, modify it to your needs, and do it yourself.

### Constraints

I enjoy modding for myself and sharing that work with others. That said, I don't enjoy customer support, and I _still_ get pings for mods I made over a decade ago.
This is a massive scale in terms of detail and it took a ton of work to set up. I want to keep making cool mods, but a mod like this has the possibility of sucking me into perpetual support and burnout, which I'm not interested in doing.

If you'd like to make a request, please consider the following constraints:

- At some point I'll stop doing requests. This mod is releasing in 2024. Please don't DM me five years from now asking for something.
- I won't make names that are inappropriate or slurs. This is something I don't have interest in, and I also want to respect the original voice actors
- English only
- Names should be between 1 and 3 syllables
- Names should be a single word
- If a name is not pronounced correctly, there is not really anything I can do within my current workflow
- If a line for a given name is not saying the name, please report it by giving me the player name as well as the line id. You can look this up in github based on the character name and words. If you don't care enough to look up the id, I won't care enough to fix the line. https://github.com/ManApart/starfield-mods/blob/master/tools/reference/sayMyName/