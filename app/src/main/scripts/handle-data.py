import requests
import shutil
from bs4 import BeautifulSoup
import pandas as pd

# Get basic data
df1 = pd.read_csv('app/src/main/assets/constellations.csv', sep=';')
headers = df1.columns.values.copy()
for index, head in enumerate(headers):
    headers[index] = head.split(" / ")[0]

cols_dict = dict(zip(df1.columns.values, headers))
df1.rename(columns=cols_dict, inplace=True)
new_names = df1['Observation Season'].apply(lambda x: x.split(' / ')[0])
new_col1_names = dict(zip(df1['Observation Season'], new_names))
df1['Observation Season'].replace(new_col1_names, inplace=True)
df1.rename(columns={
    'IAU code': 'Id',
    'Observation Season': 'ObservationSeason',
    'Latin name': 'Name',
    'Constellation area in square degrees': 'Area',
    'Dec (Declinaison)': 'Declination',
    'RA (Right Ascension': 'RightAscension',
    'Principal star': 'PrincipalStar',
    'Constellation zone (celestial equator)': 'CelestialEquatorZone',
    'Constellation zone (ecliptic)': 'EclipticZone',
    'Quad': 'Quadrant',
    'Name origin': 'NameOrigin'
}, inplace=True)
df1['Id'] = df1['Id'].apply(str.lower)
df1.drop(['French name', 'English name', 'Image'], axis=1, inplace=True)

# Get description data
URL = 'https://in-the-sky.org//data/constellations_list.php?skin=1'
html = requests.get(URL)
soup = BeautifulSoup(html.text, features="lxml")
table = soup.find('table', {"class": "stripy bordered bordered2"})
headers_html = table.find_all(lambda tag: tag.name == 'thead')
rs = headers_html[0].find_all('td')
headers = []
for df2 in rs:
    headers.append(df2.text)
headers[0] = 'Name'
headers[1] = 'Story'
rows_data = []
table_body = soup.find('tbody')
for row in table_body.find_all('tr'):
    row_data = []
    for cell in row.find_all('td'):
        row_data.append(cell.text.strip())
    rows_data.append(row_data)
df2 = pd.DataFrame(data=rows_data, columns=headers)
df2.loc[df2['Name'] == 'Serpens Caput', 'Story'] = '{} {}'.format(
    df2[df2['Name'] == 'Serpens Caput']['Story'].values[0].strip(),
    df2[df2['Name'] == 'Serpens Cauda']['Story'].values[0].strip())
df2.drop(df2[df2['Name'] == 'Serpens Cauda'].index[0],
         axis=0, inplace=True)
df2.loc[df2['Name'] == 'Serpens Caput', 'Name'] = 'Serpens'
df2.drop(columns=['Genitive Form', 'Brightest Star'], inplace=True)
df2.rename(columns={'First Appeared': 'FirstAppeared'}, inplace=True)

# Get image and meaning data
ROOT = "https://www.iau.org"

html = requests.get(
    ROOT + "/public/themes/constellations/")
soup = BeautifulSoup(html.text, features="lxml")
links = soup.find_all('a', string='GIF')

names = {}
for row in soup.find_all('tr'):
    row_data = row.find_all('td')
    iau_code = row_data[1].text.lower().strip()
    names[iau_code] = row_data[2].text.strip()

data = []
for link in links:
    line = []
    iau_code = link.get('href').split('/')[-1].split('.')[0].lower().strip()

    if iau_code.startswith('ser'):
        iau_code = 'ser'

    if iau_code in [item[0] for item in data]:
        continue

    image_url = ROOT + link.get('href')
    data.append([iau_code, names[iau_code], image_url])

df3 = pd.DataFrame(data, columns=['Id', 'Meaning', 'Image'])

# df1.to_csv('app/src/main/assets/stars1.csv',
#            encoding='utf-8-sig', float_format='%g', index=False)
# df2.to_csv('app/src/main/assets/stars2.csv', encoding='utf-8-sig', index=False)
# df3.to_csv('app/src/main/assets/stars3.csv', encoding='utf-8-sig', index=False)

# Combine and save data
df4 = pd.merge(df1, df3, on='Id')
df = pd.merge(df4, df2, on='Name')
headers = df.columns
ids = df['Id']
df = df.drop(columns=['Id'])
df.insert(loc=0, column='Id', value=ids)

# Drop columns we don't need or want:
to_drop = [
    "Constellation area in % of the celestial sphere",
    "Constellation zone (Milky Way)"
]

df.drop(columns=to_drop, inplace=True)

# To drop columns for testing
# columns = [
#     'Id',
#     'Observation season',
#     'Name',
#     'Constellation area in square degrees',
#     'Declination',
#     'Right ascension',
#     'Principal star',
#     'Constellation zone (celestial equator)',
#     'Constellation zone (ecliptic)',
#     'Quadrant',
#     'Name origin',
#     'Meaning',
#     'Image',
#     'Story',
#     'First appeared'
# ]

# df.drop(columns=columns, inplace=True)

df.to_csv('app/src/main/assets/data.csv', encoding='utf-8-sig',
          float_format='%g', index=False)

df.to_json('app/src/main/assets/constellation_data.json', orient='records')

# Download images
# for index, row in df.iterrows():
#     # This is the image url.
#     image_url = row['Image']

#     # Open the url image, set stream to True, this will return the stream content.
#     resp = requests.get(image_url, stream=True)

#     # Open a local file with wb ( write binary ) permission.
#     local_file = open('{}_image.png'.format(row['Id']), 'wb')

#     # Set decode_content value to True, otherwise the downloaded image file's size will be zero.
#     resp.raw.decode_content = True

#     # Copy the response stream raw data to local image file.
#     shutil.copyfileobj(resp.raw, local_file)

#     # Remove the image url response object.
#     del resp
