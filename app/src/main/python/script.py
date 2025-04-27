# import yt_dlp
# import json
# import os
# from com.chaquo.python import Python
#
# # Get app's internal storage path
# app_context = Python.getPlatform().getApplication()
# internal_storage = app_context.getFilesDir().getAbsolutePath()
# download_path = os.path.join(internal_storage, "downloads")
# merged_path=os.path.join(internal_storage, "merged")
#
#
# # Ensure the directory exists
# os.makedirs(download_path, exist_ok=True)
# os.makedirs(merged_path, exist_ok=True)
#
# #funtion to get merged_path
# def get_merged_path():
#     return merged_path
#
# # Function to get available video formats
# def get_video_formats(url):
#     formats_list=[]
#     ydl_opts = {'noplaylist': True}
#
#     with yt_dlp.YoutubeDL(ydl_opts) as ydl:
#         info = ydl.extract_info(url, download=False)  # Fetch video info
#         formats = info.get('formats', [])
#
#     # Extract necessary format details
#     for fmt in formats:
#         formats_list.append({
#             "format_id": fmt.get("format_id", ""),
#             "ext": fmt.get("ext", ""),
#             "resolution": fmt.get("resolution") or "audio",
#             "fps": fmt.get("fps", ""),
#             "vcodec": fmt.get("vcodec",""),
#             "acodec": fmt.get("acodec",""),
#             "filesize": fmt.get("filesize", "Unknown")
#         })
#     return formats_list
#
#
#
# def download_video(url, format,callback):
#     def hook(d):
#         if d['status'] == 'downloading':
#             callback(json.dumps(d))
#     if(format in ["",None,"Unknown","None"]):
#         ydl_opts = {
#                 'progress_hooks':[hook],
#                 'outtmpl': os.path.join(download_path, "%(title)s.%(ext)s"),
#                 'noplaylist': True
#             }
#     else:
#         ydl_opts = {
#             'progress_hooks':[hook],
#             'format': f"{format}",
#             'outtmpl': os.path.join(download_path, "%(title)s.%(ext)s"),
#             'noplaylist': True
#         }
#     file_path=""
#     with yt_dlp.YoutubeDL(ydl_opts) as ydl:
#         info_dict = ydl.extract_info(url, download=True)  # Get video info
#         file_path = ydl.prepare_filename(info_dict)  # Get file path
#     return file_path
import yt_dlp
import json
import os
from com.chaquo.python import Python

# Get app's internal storage path
app_context = Python.getPlatform().getApplication()
internal_storage = app_context.getFilesDir().getAbsolutePath()
download_path = os.path.join(internal_storage, "downloads")
merged_path=os.path.join(internal_storage, "merged")

# Ensure the directory exists
os.makedirs(download_path, exist_ok=True)
os.makedirs(merged_path, exist_ok=True)

#funtion to get merged_path
def get_storage_path():
    return internal_storage


qualityList = [2160, 1440, 1080, 720, 480, 360, 240, 144]
audioQualityList = ["High", "Medium", "Low"]
noneList = ["none", "None", "Unknown", "",None]

def get_requested_format(videoQuality, audioQuality, formats):
    if formats==[]:
        print("Not formats provided")
        return [None,None]
    videoFormat=None
    audioFormat=None

    videoFormats=[f for f in formats if (f.get("vcodec","") not in noneList and f.get("height","") not in noneList)]

    if len(videoFormats)==0:
        print("No video format found but still trying to get the best quality without widht and height")
        videoFormats=[f for f in formats if (f.get("vcodec","") not in noneList)]

    if len(videoFormats)==0:
        print("No video format found returning None")
        return [None,None]

    if videoQuality not in qualityList:
        print("Invalid video quality continue with default")
        videoQuality=2160

    qualityVideoFormats=[f for f in videoFormats if (f.get("height","")==videoQuality)]

    if len(qualityVideoFormats)==0:
        print("Exact video quality not found still trying to get the best quality")
        videoIndex=qualityList.index(videoQuality)
        while(qualityVideoFormats==[] and videoIndex<len(qualityList)-1):
            qualityVideoFormats=[f for f in videoFormats if (f.get("height",0)!=0 and f.get("height",0)<=qualityList[videoIndex] and f.get("height",0)>=qualityList[videoIndex+1])]
            if len(qualityVideoFormats)!=0:
                break
            print(f"{videoQuality} not found decreasing quality")
            videoIndex=videoIndex+1
            videoQuality=qualityList[videoIndex]

    if len(qualityVideoFormats)==0:
        print("No known video quality found still trying to get the best quality")
        qualityVideoFormats=videoFormats

    knownFileSizeVideoFormats=[f for f in qualityVideoFormats if f.get("filesize","") not in noneList]
    if len(knownFileSizeVideoFormats)==0:
        print("No known file size found still trying to get the best quality")
        knownFileSizeVideoFormats=qualityVideoFormats

    videoFormat=knownFileSizeVideoFormats[0]

    audioFormats=[f for f in formats if f.get("acodec","") not in noneList]
    if len(audioFormats)==0:
        print("No audio format found")
        return [None,None]


    if audioQuality not in audioQualityList:
        print("Invalid audio quality continue with default")
        audioQuality="High"

    audioFormatWithABR=[f for f in audioFormats if f.get("abr","") not in noneList]
    if len(audioFormatWithABR)==0:
        print("No known audio quality found still trying to get the best quality")
        audioFormatWithABR=audioFormats


    qualityAudioFormats=[]

    if audioQuality=="High":
        qualityAudioFormats=[f for f in audioFormatWithABR if f.get("abr") and isinstance(f["abr"], (int, float)) and f["abr"] >= 196]

    elif audioQuality=="Medium":
        qualityAudioFormats=[f for f in audioFormatWithABR if (f.get("abr") and isinstance(f["abr"], (int, float)) and f["abr"]>=128 and f["abr"]<196)]

    elif audioQuality=="Low":
        qualityAudioFormats=[f for f in audioFormatWithABR if (f.get("abr") and isinstance(f["abr"], (int, float)) and f["abr"]<128)]

    if len(qualityAudioFormats)==0:
        print("Exact audio quality not found still trying to get the best quality")
        qualityAudioFormats=audioFormatWithABR

    knownFileSizeAudioFormats=[f for f in qualityAudioFormats if f.get("filesize","") not in noneList]
    if len(knownFileSizeAudioFormats)==0:
        print("No known file size found still trying to get the best quality")
        knownFileSizeAudioFormats=audioFormatWithABR

    audioFormat=knownFileSizeAudioFormats[0]

    finalVideoFormat = {}
    finalAudioFormat = {}
    finalVideoFormat["format"]=videoFormat.get("format_id", "")
    finalVideoFormat["width"]=videoFormat.get("width", 0)
    finalVideoFormat["height"]=videoFormat.get("height", 0)
    finalVideoFormat["filesize"]=videoFormat.get("filesize", 0)
    finalAudioFormat["format"]=audioFormat.get("format_id", "")
    finalAudioFormat["abr"]=audioFormat.get("abr", 0)
    finalAudioFormat["filesize"]=audioFormat.get("filesize", 0)

    return [finalVideoFormat,finalAudioFormat]




def get_video_info(url,videoQuality=2160,audioQuality="High"):
    videoDetails = {}

    ydl_opts = {
        'quiet': True,
        'skip_download': True,  # Don't download the video
    }

    retry = 0
    while retry < 3:
        with yt_dlp.YoutubeDL(ydl_opts) as ydl:
            info = ydl.extract_info(url,download=False)
            videoDetails["title"] = info.get('title') or info.get('fulltitle', "")
            videoDetails["url"] = url or info.get('webpage_url', "")
            videoDetails["thumbnail"] = info.get('thumbnail') or (info.get('thumbnails')[0]['url'] if info.get('thumbnails') else "")
            videoDetails["duration"] = info.get('duration_string') or f"{info.get('duration', 0)//60}:{info.get('duration', 0)%60}"
            videoDetails["extractor"] = info.get('extractor_key') or info.get('extractor', "")
            videoDetails["uploader"] = info.get('uploader') or info.get('uploader_id') or info.get('channel', "")
            videoDetails["requested_format"]=get_requested_format(videoQuality,audioQuality,info.get('formats',[]))
            if videoDetails["requested_format"]!=[None,None]:
                break
            retry+=1
            print(f"Retry {retry} for getting video details")


    return json.dumps(videoDetails)



def download_format(url, format,callback):
    def hook(d):
        if d['status'] == 'downloading':
            callback(json.dumps(d))
    if(format in noneList):
        ydl_opts = {
                'progress_hooks':[hook],
                'outtmpl': os.path.join(download_path, "%(title)s.%(ext)s"),
                'noplaylist': True,
            }
    else:
        ydl_opts = {
            'progress_hooks':[hook],
            'format': f"{format}",
            'outtmpl': os.path.join(download_path, "%(title)s.%(ext)s"),
            'noplaylist': True,
        }
    file_path=""
    with yt_dlp.YoutubeDL(ydl_opts) as ydl:
        info_dict = ydl.extract_info(url, download=True)  # Get video info
        file_path = ydl.prepare_filename(info_dict)  # Get file path
    return file_path

# # def callable(data):
# #     pass
#
# url="https://www.youtube.com/watch?v=3cZNbwTXixU"
#
# # download_video(url,"",callable)
# print(get_video_info(url,2160,"High"))