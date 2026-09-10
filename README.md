# Mekanism Everything Ore Processing

Mekanism のジョークアドオン。**あらゆるアイテムを「なんでも原石（Everything Raw Ore）」に変え、Mekanism の鉱石処理チェーンに乗せて増やし、元のアイテムに戻す。**

- Minecraft 1.21.1 / NeoForge 21.1 / Mekanism 10.7
- ライセンス: MIT（ベーステクスチャは MIT の Mekanism のものを灰色化して使用）

## 流れ

1. 作業台で、中央にオスミウム原石・周囲 8 マスにガラス → **なんでも原石（元情報なし）×1**
2. 作業台で、中央に元情報なしのなんでも原石・周囲 8 マスに同じ任意アイテム → **そのアイテムのなんでも原石 ×8**
3. Mekanism の機械で加工（比率は本家の原石と同じ）
   - Energized Smelter / かまど: 原石 → インゴット（1x）
   - Enrichment Chamber: 原石3 → ダスト4（約1.33x）
   - Purification Chamber: 原石 + O₂ → クランプ2（2x）
   - Chemical Injection Chamber: 原石3 + HCl → シャード8（約2.67x）
   - Dissolution → Washer → Crystallizer: 原石3 + H₂SO₄ → スラリー 2000mB → 結晶10（約3.33x）
4. **なんでもインゴット** を作業台に 1 個置く → **元アイテム**に復元

全ての「なんでも○○」は Data Component `original_item` に元アイテムを保持する。スラリー段階では化学物質に情報を載せられないため、**全アイテム分の Dirty / Clean スラリーを起動時に動的登録**し、スラリーの種類そのもので元アイテムを表す。

## Config

| ファイル | キー | 内容 |
|---|---|---|
| `*-startup.toml` | `slurryNamespaceWhitelist` | スラリーを生成するアイテムの namespace（空 = 全部）。超大型パックで絞る用 |
| `*-common.toml` | `originStorageMode` | `FULL_STACK`（Component も保持）/ `ITEM_ID`（アイテム種類のみ） |
| `*-common.toml` | `allowNesting` / `maxNestingDepth` | なんでも○○自身を再変換できるか・その深さ |
| `*-common.toml` | `blacklist` | 変換禁止アイテム（タグ `mekanismeverythingoreprocessing:blacklist` も有効） |

## 開発

```
gradlew.bat build       # ビルド
gradlew.bat runData     # src/generated/resources を再生成
gradlew.bat runClient   # クライアント起動
```

- ベーステクスチャの再生成: `scripts/generate_base_textures.ps1`（`D:\Mekanism` のソースが必要）
