# なんでも○○のベーステクスチャを Mekanism (MIT) の灰色ベースから作る。
# 目的: ティント（power 由来の色）を乗せる前提の、彩度ゼロの 16x16 PNG を用意する。
# 前提: D:\Mekanism にソースがあること。
# 結果: src/main/resources/assets/mekanismeverythingoreprocessing/textures/{item,block}/ に PNG を出力する。

param(
    [string]$MekanismRoot = "D:\Mekanism\src\main\resources\assets\mekanism\textures",
    [string]$OutRoot = (Join-Path $PSScriptRoot "..\src\main\resources\assets\mekanismeverythingoreprocessing\textures")
)

Add-Type -AssemblyName System.Drawing

function Convert-ToGray {
    param([string]$Source, [string]$Dest)

    $src = [System.Drawing.Bitmap]::FromFile($Source)
    $dst = New-Object System.Drawing.Bitmap $src.Width, $src.Height, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    for ($y = 0; $y -lt $src.Height; $y++) {
        for ($x = 0; $x -lt $src.Width; $x++) {
            $c = $src.GetPixel($x, $y)
            # 輝度で灰色化（アルファは維持）
            $lum = [int][Math]::Round(0.299 * $c.R + 0.587 * $c.G + 0.114 * $c.B)
            $dst.SetPixel($x, $y, [System.Drawing.Color]::FromArgb($c.A, $lum, $lum, $lum))
        }
    }
    $dir = Split-Path $Dest -Parent
    New-Item -ItemType Directory -Force $dir | Out-Null
    $dst.Save($Dest, [System.Drawing.Imaging.ImageFormat]::Png)
    $src.Dispose()
    $dst.Dispose()
    Write-Host "wrote $Dest"
}

# アイテム: Mekanism 側に灰色ベースがあるものはそのまま灰色化して使う
$itemMap = @{
    "everything_dust"       = "item\dust.png"
    "everything_dirty_dust" = "item\dirty_dust.png"
    "everything_clump"      = "item\clump.png"
    "everything_shard"      = "item\shard.png"
    "everything_crystal"    = "item\crystal.png"
    "everything_ingot"      = "item\ingot.png"
    "everything_nugget"     = "item\nugget.png"
    # 原石は灰色ベースが無いので、オスミウム原石を脱色して使う
    "everything_raw_ore"    = "item\raw_osmium.png"
}
foreach ($name in $itemMap.Keys) {
    Convert-ToGray -Source (Join-Path $MekanismRoot $itemMap[$name]) -Dest (Join-Path $OutRoot "item\$name.png")
}

# ブロック: オスミウムブロックを脱色
Convert-ToGray -Source (Join-Path $MekanismRoot "block\block_osmium.png") -Dest (Join-Path $OutRoot "block\everything_block.png")
